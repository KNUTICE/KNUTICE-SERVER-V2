package crawler.worker;

import crawler.worker.model.NoticeDto;
import db.domain.notice.MaxNoticeIdsDocument;
import db.domain.notice.MaxNoticeIdsMongoRepository;
import db.domain.notice.NoticeDocument;
import db.domain.notice.NoticeMongoRepository;
import db.domain.notice.dto.MaxNttIdsDto;
import global.utils.NoticeMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Slf4j
@RequiredArgsConstructor
public class NoticeProcessor {

    private final NoticeMongoRepository noticeMongoRepository;
    private final MaxNoticeIdsMongoRepository maxNoticeIdsRepository;
    private static final Map<NoticeMapper, List<NoticeDto>> newNoticeDtoTable = new ConcurrentHashMap<>();

    /*
     * 클래스가 최초 한 번 로딩될 때, 초기화
     * @author itstime0809
     */
    static {
        createNewNoticeTitlesTable();
    }

    private static void createNewNoticeTitlesTable() {
        Stream.of(NoticeMapper.values())
                .forEach(noticeMapperData -> {
                    newNoticeDtoTable.computeIfAbsent(noticeMapperData, key -> new ArrayList<>());
                        }
                );
    }
    @Transactional
    public boolean noticeUpdate(final List<NoticeDto> noticeList, final NoticeMapper noticeMapper, final boolean boot) {
        log.info("Notice : {}\nnoticeList Size : {}", noticeMapper, noticeList.size());


        /*
         * 1. 서버가 켜진 상태와 서버가 켜진후의 상태 모두
         *    디비에서 이전 maxNttId 를 추출합니다.
         */
        List<Long> maxNttIds = getMaxNttIds(noticeMapper);

        /*
         * 2. 공지와 게시글의 nttId 를 분리합니다.
         * */
        long announcementMaxNttId = maxNttIds.get(0);
        long noticeMaxNttId = maxNttIds.get(1);

        log.info("Notice : {} \n announcementMaxNttId : {} \n noticeMaxNttId : {}", noticeMapper, announcementMaxNttId, noticeMaxNttId);

        /*
         * 3. maxNttId 를 업데이트 하기 위해서, 변수를 생성합니다.
         *    레이스 컨디션 때문에, 동시성 문제가 발생할 여지가 존재하지 않다고 판단하나, 안전성을 위해서 AtomicLong 을 사용합니다.
         */
        AtomicLong targetMaxNttIdForAnnouncement = new AtomicLong(announcementMaxNttId);
        AtomicLong targetMaxNttIdForNotice = new AtomicLong(noticeMaxNttId);


        /*
         * 4. 서버가 처음 시작되어 아래 메소드를 수행하는 경우,
         *    서버가 처음 시작된 후 아래 메소드를 수행하는 경우로 나눠서 볼 수 있습니다.
         *    두 경우 모두, 중복성 여부를 검사한 뒤, maxNttId 를 업데이트 하는 로직을 거치고,
         *
         *    새로운 게시글을 저장합니다.
         *    새로운 게시글을 저장할 때, 서버가 처음 시작된 경우, 모든 게시글을 가지고 오서 저장합니다. 이는 디비에서 누락된 게시글이 존재할 수 있는 경우를 방지하기 위해서입니다.
         *    새로운 게시글을 저장할 때, 서버가 처음 시작되지 않은 경우, 새로운 게시글만을 받기 때문에, 새로운 게시글을 그대로 저장합니다.
         *
         *    Fcm 전송을 위한 title 추출을 위해서, 서버가 처음 켜지는 경우는 보내지 않기 때문에 addNoticeTitle 메소드 수행이 필요없습니다.
         *    따라서, 서버가 처음 시작된 이후에만 title 을 추출합니다.
         */


        List<NoticeDto> fcmNoticeList = new ArrayList<>();
        noticeList.forEach(noticeDto -> {
            boolean isDuplication = noticeDto.isDuplication();
            boolean isSave = changeMaxNttId(noticeDto, announcementMaxNttId, noticeMaxNttId, targetMaxNttIdForAnnouncement, targetMaxNttIdForNotice, isDuplication);
            // save 대상이라면
            if(isSave) {
                saveNoticeDocument(noticeDto);
            }
            if (!boot) {
                log.info("New fcm titles : {}", noticeDto.getTitle());
                addNoticeDto(noticeDto, fcmNoticeList);
            }
        });

        /*
         * targetMaxNttId 는 공지와 게시글 모두, 가장 큰 maxNttId 로 업데이트가 되어 있기 때문에, maxNttId 를 업데이트합니다.
         */

        log.info("Notice : {}, AnMaxNttId : {} \n NoMaxNttId : {}", noticeMapper, targetMaxNttIdForAnnouncement, targetMaxNttIdForNotice);

        MaxNoticeIdsDocument maxNoticeIdsDocument = maxNoticeIdsRepository.findById(noticeMapper).get();
        maxNoticeIdsDocument.setAnnouncementMaxNttId(targetMaxNttIdForAnnouncement.get());
        maxNoticeIdsDocument.setNoticeMaxNttId(targetMaxNttIdForNotice.get());

        maxNoticeIdsRepository.save(maxNoticeIdsDocument);


        /*
         * 처음시작된 경우, 게시글을 삭제하지 않아도, 중복된 게시글을 저장되지 않기 때문에, boot = false 인 경우만, 삭제한다.
         */
//        if (!boot) {
//            deleteOldestNotice(noticeType, (byte) noticeList.size());
//        }

        /**
         * 새로운 게시글의 제목이 fcmNoticeList 에 저장되고, 그 값을 newNoticeTables 에 저장.
         */
        newNoticeDtoTable.put(noticeMapper, fcmNoticeList);
        return fcmNoticeList.isEmpty();
    }




    private boolean changeMaxNttId(NoticeDto noticeDto, long announcementMaxNttId, long noticeMaxNttId,
                                   AtomicLong targetMaxNttIdForAnnouncement, AtomicLong targetMaxNttIdForNotice, boolean isDuplication) {

        long newNoticeNttId = noticeDto.getNttId();
        boolean isSaveNotice = false;

        // 중복 처리 여부와 관계없이, 공지인지 게시글인지를 먼저 구분
        boolean isAnnouncement = noticeDto.getContentNumber() == -1;
        boolean isNewNttIdGreaterThanAnnouncement = newNoticeNttId > announcementMaxNttId;
        boolean isNewNttIdGreaterThanNotice = newNoticeNttId > noticeMaxNttId;

        if (isDuplication) {
            log.info("Checking duplication Notice");

            // 만약, 둘 중에 하나라도, 틀리다면,
            if (isNewNttIdGreaterThanAnnouncement && isNewNttIdGreaterThanNotice) {
                targetMaxNttIdForAnnouncement.set(newNoticeNttId);
                targetMaxNttIdForNotice.set(newNoticeNttId);
                log.info("Both Announcement and Notice max NTT IDs updated to: {}", newNoticeNttId);
                isSaveNotice = true;
            } else if (isNewNttIdGreaterThanNotice) {
                targetMaxNttIdForNotice.set(newNoticeNttId);
                log.info("Notice max NTT ID updated to: {}", newNoticeNttId);
                isSaveNotice = true;
            } else if (isNewNttIdGreaterThanAnnouncement) {
                targetMaxNttIdForAnnouncement.set(newNoticeNttId);
                log.info("Announcement max NTT ID updated to: {}", newNoticeNttId);
                isSaveNotice = true;
            }

        } else {
            log.info("Checking non-duplication Notice");

            if (isAnnouncement && isNewNttIdGreaterThanAnnouncement) {
                targetMaxNttIdForAnnouncement.set(newNoticeNttId);
                log.info("Non-duplicate Announcement max NTT ID updated to: {}", newNoticeNttId);
                isSaveNotice = true;
            } else if (!isAnnouncement && isNewNttIdGreaterThanNotice) {
                targetMaxNttIdForNotice.set(newNoticeNttId);
                log.info("Non-duplicate Notice max NTT ID updated to: {}", newNoticeNttId);
                isSaveNotice = true;
            }
        }

        if (isSaveNotice) {
            log.info("isSaveNotice is true");
            return true;
        }

        return false;
    }

    public Optional<List<NoticeDto>> getUpdateNoticeMap(NoticeMapper noticeMapper) {
        log.info("{} Execute getUpdateNoticeMap", noticeMapper);
        List<NoticeDto> newNoticeDtoList = newNoticeDtoTable.get(noticeMapper);

        // 게시글이 존재한다면
        if(!newNoticeDtoList.isEmpty()) {
            log.info("{} newTitleList is not empty", noticeMapper);
            newNoticeDtoList.forEach(data -> {
                log.info("title and Size : {}", data.getTitle());
            });
        } else {
            return Optional.empty();
        }
        // 리스트를 초기화
        newNoticeDtoTable.put(noticeMapper, new ArrayList<>());
        if(newNoticeDtoTable.get(noticeMapper).isEmpty()) {
            log.info("{} newNoticeTitles table values initialized", noticeMapper);
        }
        return Optional.of(newNoticeDtoList);
    }


    /**
     * for 문에서 참조로 전달한 crawling news 객체.
     */

    public void saveNoticeDocument(NoticeDto noticeDto) {
        log.info("SaveNoticeEntity nttId : {}, notice : {}", noticeDto.getNttId(), noticeDto.getNoticeMapper());
        noticeMongoRepository.save(
                NoticeDocument.builder()
                        .noticeName(noticeDto.getNoticeMapper())
                        .nttId(noticeDto.getNttId())
                        .title(noticeDto.getTitle())
                        .contentNumber(noticeDto.getContentNumber())
                        .contentUrl(noticeDto.getContentUrl())
                        .contentImage(noticeDto.getContentImage())
                        .departmentName(noticeDto.getDepartName())
                        .registeredAt(noticeDto.getRegisteredAt())
                        .build());
    }

    /**
     * 새로운 게시글에 대한 fcm 전송용 title 추출.
     *
     * @author itstime0809
     */
    private void addNoticeDto(final NoticeDto noticeDto, List<NoticeDto> fcmNoticeList) {
        log.info("Notice : {} Execute addNoticeTitle : {}", noticeDto.getNoticeMapper(), noticeDto.getTitle());
        fcmNoticeList.add(noticeDto);
    }

    /**
     * max_notice_ids 테이블에서, 공지와 게시글 각 maxNttIds 를 추출.
     *
     * @author itstime0809
     */
    private List<Long> getMaxNttIds(final NoticeMapper noticeMapper) {
        Optional<MaxNoticeIdsDocument> maxNoticeIdsDocument = maxNoticeIdsRepository.findById(noticeMapper);

        // MaxNoticeIdsDocument를 MaxNttIdsDto로 변환
        MaxNttIdsDto maxNttIdsDto = maxNoticeIdsDocument
            .map(doc -> MaxNttIdsDto.builder()
                .announcementMaxNttId(Optional.ofNullable(doc.getAnnouncementMaxNttId()).orElse(0L))
                .noticeMaxNttId(Optional.ofNullable(doc.getNoticeMaxNttId()).orElse(0L))
                .build())
            .orElseThrow(() -> new RuntimeException("Not Found Max NttIds"));

        // List<Long> 형태로 변환하여 반환
        return Arrays.asList(maxNttIdsDto.getAnnouncementMaxNttId(), maxNttIdsDto.getNoticeMaxNttId());
    }
}

