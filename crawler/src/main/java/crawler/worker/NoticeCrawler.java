package crawler.worker;

import com.google.firebase.messaging.FirebaseMessagingException;
import crawler.worker.model.NoticeDto;
import global.ifs.SchedulingOperation;
import global.utils.NoticeMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * NoticeCrawler 클래스는 SchedulingOperation 인터페이스를 구현합니다.
 * scheduling() 메소드를 구현하게 되며, NoticeCrawler 뿐만 아닌 다른 Scheduling 연산이 필요한것을 생각하여 구현했습니다.
 * @author itstime0809
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class NoticeCrawler implements SchedulingOperation {

    // 서버 첫 실행 여부
    private static boolean boot = true;

    private final NoticeUpdater noticeUpdater;


    /**
     * 각 공지의 noticeType을 키로하여, 값은 새로운 noticeDto를 저장할 자료구조인 Set을 사용합니다.
     * Set 자료구조는 중복을 제거할때 내부적으로 eqauls() 메소드를 호출합니다. 그래서 NoticeDto는 equals, hashcode 오버라이드 되어 nttId로 중복을 검사합니다.
     * 따라서 nttId 같은 noticeDto 단 하나만 남게됩니다.
     * @author itstime0809
     */

    private final Map<NoticeMapper, Set<NoticeDto>> newNoticeDtoTable = new ConcurrentHashMap<>();


    /**
     * 각 공지의 noticeType을 키로하여, 값은 공지와 게시글에 대해서 이전 nttId 저장합니다.
     * List 자료구조에서, 0번째 index는 공지의 beforeNttId를 의미하며, 1번째 index는 게시글의 beforeNttId를 의미합니다.
     * 서버가 처음 시작되면, 각각 이전 nttId를 저장한 상태가 되고, 후에 executeOnServerRunning 메소드에서는 매번 해당 테이블에 접근하여, nttId를 비교합니다.
     * @author itstime0809
     */
    private final Map<NoticeMapper, List<Long>> noticeAndAnnouncementIdTable = new ConcurrentHashMap<>();


    /**
     * Stream 병렬처리를 하는 경우, List<List<NoticeDto>> 자료구조가 가독성이 좋지 않아, 내부 클래스로 구현했습니다.
     * @author itstime0809
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class ParsedAllNotice {
        private List<List<NoticeDto>> noticeDtoList;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 */15 * * * *") // 24시간 15분 간격 실행 - 2025.03.26 수정
    public void scheduling() {
        try {
            doCrawling();
            if (boot) {
                boot = false;
            }
        } catch (FirebaseMessagingException | NullPointerException | IOException e) {
            log.error("Error during scheduling", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Crawling 작업의 시작입니다.
     * SchedulingOperation 메소드인 scheduling() 오버라이드하여 정해진 시간 마다 스케줄링을 시도합니다.
     * dorCrawling() 은 하나의 스레드로 부터 시작되며, 내부에서 Stream parallel() 병렬처리를 사용합니다.
     * parallel() 사용시, 각 스레드별로 Task를 진행하기 때문에 공지의 개수 만큼, 크롤링 작업을 시작합니다.
     *
     * [ 서버가 처음 시작된 경우]
     * boot == true 이며, 처음 시작 된 경우는 항상 모든 게시글을 크롤링합니다. 공지와, 게시글을 구분해서 크롤링하지 않고, 중복만 제거합니다.
     * 따라서 공지에 있고, 게시글에 없다면, 공지 게시글이 포함되며, 공지에 있지 않고 게시글에는 있다면 게시글이 포함되고, 공지와 게시글이 모두 존재한다면, 중복을 제거한 단 하나의 게시글 객체만 포함합니다.
     * 크롤링 작업후에 beforeMaxNttId 역할을 하는 firstNttId를 공지와 게시글을 구분해서 저장합니다. 이는 executeOnServerRunning 메소드가 동작될때 마다, beforeMaxNttId 역할을합니다.
     *
     *
     * [ 서버가 처음 시작되지 않은 경우 ]
     * boot == false 이며, 처음 시작 된 후를 의미합니다. 이때는 executeOnServerRunning() 메소드가 지정된 시간마다 수행되며, [공지]와 게시글이 비동기로 작업을 시도합니다.
     * CompletableFuture 를 사용하여 비동기 처리를 진행하고 있고, 실행 순서는 보장하지 않습니다. 각 작업이 완료된 후에는 afterTask 작업에서, 공지 그리고 게시글에서 작업한 NoticeDto를
     * 조건에 만족하고, 중복을 제거한 상태의 객체들을 리스트로 변환 후, 반환합니다.
     *
     * @author itstime0809
     *
     */

    private void doCrawling() throws FirebaseMessagingException, NullPointerException, IOException {
        ParsedAllNotice parsedAllNotice = ParsedAllNotice.builder()
                .noticeDtoList(Stream.of(
                                NoticeMapper.GENERAL_NEWS,
                                NoticeMapper.SCHOLARSHIP_NEWS,
                                NoticeMapper.EVENT_NEWS,
                                NoticeMapper.ACADEMIC_NEWS
//                                NoticeMapper.EMPLOYMENT_NEWS
                        )
                        .parallel()
                        .map(noticeMapper -> {
                            try {
                                if (boot) {
                                    return executeOnServerStart(noticeMapper);
                                }
                                return executeOnServerRunning(noticeMapper);
                            } catch (IOException e) {
                                log.error("{} Parallel error", noticeMapper);
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(Objects::nonNull) // guarantee noticeDtoList is not null
                        .filter(noticeDtoList -> !noticeDtoList.isEmpty()) // Then filter out empty strings
                        .collect(Collectors.toList())).build();

        // guarantee not build failed
        if(parsedAllNotice != null) {
            deliverTitlesToWorker(parsedAllNotice);
        }

    }



    private void deliverTitlesToWorker(final ParsedAllNotice parsedAllNotice)  {
        parsedAllNotice.getNoticeDtoList().forEach(noticeDtoList -> {
            try {
                noticeUpdater.updateNoticeTitles(noticeDtoList, noticeDtoList.get(0).getNoticeMapper(), boot);
            } catch (FirebaseMessagingException e) {
                log.error("Occur FirebaseMessagingException");
                throw new RuntimeException(e);
            }
        });
    }

    private Document connectNoticeUrl(final NoticeMapper noticeMapper) throws IOException {
        return Jsoup.connect(noticeMapper.getNoticeUrl()).get();
    }

    /**
     * bool == true 경우, 크롤링을 수행합니다. 서버가 시작되고 최초 단 한 번만 수행하며, 이후에는 executeOnServerRunning 만 수행됩니다.
     * 최초 크롤링시에는, 데이터베이스에 게시글을 저장하기 위한 작업을 수행합니다. 유일키로인해, 중복게시글이 저장되지 않기 때문에, 첫 번째 페이지 모든 게시글을 크롤링하여 저장합니다.
     * 해당 작업을 수행하는 이유는, 데이터베이스에 게시글이 존재하지 않을 경우, 데이터를 삽입하기 위함입니다.
     *
     * 처음 크롤링 대상은 상단의 [공지] 첫 번째 'tr' 후에 상단 게시글 첫 번째 'tr'에 대해서, nttId를 table 에 저장합니다.
     * 저장하는 이유는, 각 공지별로, 이전에 크롤링한 가장큰 nttId 를 식별하고, 비교하기 위함입니다.
     *
     *
     * @author itstime0809
     */
    private List<NoticeDto> executeOnServerStart(final NoticeMapper noticeMapper) throws IOException {
        boolean isAnnouncement = false;
        boolean isNotice = false;

        Set<NoticeDto> noticeDtoSet = new HashSet<>();
        Document document = connectNoticeUrl(noticeMapper);

        Elements contents = document.select("tbody > tr");


        for (Element element : contents) {
            Element contentNumber = element.select("td.problem_number").first();
            if (contentNumber != null && !contentNumber.children().isEmpty()) {
                Optional<Long> optionalId = extractAnnouncementNttId(element);
                if (optionalId.isPresent()) {
                    Long id = optionalId.get();
                    List<Long> idList = noticeAndAnnouncementIdTable.computeIfAbsent(noticeMapper, k -> new ArrayList<>(Arrays.asList(null, null)));
                    log.info("Notice Type : {} Server Start idList : {}, index(0 Announcement) {}", noticeMapper, idList, idList.get(0));
                    if (!isAnnouncement) {
                        idList.set(0, id);
                        isAnnouncement = true;
                    }
                    Optional<NoticeDto> noticeDto = createNoticeDto(noticeMapper, element, id, true);
                    noticeDto.ifPresent(noticeDtoSet::add);
                }
            } else {
                Optional<Long> optionalId = extractNoticeNttId(element);
                if (optionalId.isPresent()) {
                    Long id = optionalId.get();
                    List<Long> idList = noticeAndAnnouncementIdTable.computeIfAbsent(noticeMapper, k -> new ArrayList<>(Arrays.asList(null, null)));
                    log.info("Notice Type : {} Server Start idList : {}, index(1 Notice) {}", noticeMapper, idList, idList.get(1));
                    if (!isNotice) {
                        idList.set(1, id);
                        isNotice = true;
                    }
                    Optional<NoticeDto> noticeDto = createNoticeDto(noticeMapper, element, id, false);
                    noticeDto.ifPresent(noticeDtoSet::add);
                }
            }
        }
        log.info("NoticeDto Data Size {}-{}", noticeMapper, noticeDtoSet.size());
        for(NoticeDto dto : noticeDtoSet) {
            log.info("Server Start NoticeDto nttId : {}", dto.getNttId());
        }
        return noticeDtoSet.isEmpty() ? null : new ArrayList<>(noticeDtoSet);
    }


    /**
     * boot == false 인 경우, 크롤링을 수행합니다. 서버가 executeOnServerStart 메소드를 실행한 이후에 모든 시간에 대한 크롤링은 해당 메소드가 실행됩니다.
     * 공지와 게시글을 구분하여 비동기로 처리합니다. CompletableFuture 를 사용했으며, 실행 순서에 대한 보장이 없습니다. 따라서 공지 또는 게시글중 하나가
     * 매번 같은 순서로 실행되지 않습니다. 하지만 두 비동기 작업은 모두, 공통 newNoticeDtoTable 을 바라봅니다. 그러므로, 어떤 작업을 먼저 수행하든, 결국 중복된 게시글은 제거됩니다.
     *
     * 4가지 상황을 고려합니다.
     * 1. 공지에는 게시글이 올라오고, 게시글에는 올라오지 않는경우
     * 2. 공지에는 게시글이 올라오지 않고, 게시글에는 올라온 경우
     * 3. 공지와 게시글 모두 올라온 경우
     * 4. 공지와 게시글 모두 올라오지 않은 경우
     *
     * 위 상황에 대한 처리를 고려하였습니다. 1번과 2번에 대하여, 공지 또는 게시글에서 비동기 작업을 수행할때, 게시글이 저장되기 때문에, 둘 중 하나라도 놓치지 않고, 게시글을 크롤링할 수 있습니다.
     * 3번에 대해서는 모두 게시글이 올라온 상태기 때문에, NoticeDto 에 equals 와 hashcode 가 오버라이드 되어 있습니다. 따라서 Set 은 내부적으로 중복 처리를 할 때, equals() 를 호출하여 중복을 판단하므로
     * nttId 에 대해서 동일성과 동등성을 판단했습니다. 그래서 nttId 가 같다면, 두 객체 중 하나는 제거됩니다.
     *
     * 4번에 대해서 공지와 게시글이 모두 올라오지 않았다는 것은, 'N' 새로운 게시글이 존재하지 않은 경우입니다. 각 비동기 작업에서는 가장 먼저, 'N' 이미지가 존재하는지를 판단하고
     * 만약 'N' 이미지가 존재하고, 이전에 크롤링하지 않았다면, 해당 게시글은 새로운 게시글이기 때문에 크롤링합니다.
     * 만약 'N' 이미지가 존재하고, 이전에 크롤링 했다면, 해당 게시글은 새로운 게시글이 아니고, 이전 주기에 크롤링을 했기 때문에, 새로운 게시글이 아닙니다.
     * ( 이 경우, 'N' 이미지가 일정 시간동안 유지되고 있음을 의미합니다. )
     *
     * 이전 게시글인지에 대한 판단 여부는, 아래와 같습니다.
     *
     * @author itstime0809
     */
    private List<NoticeDto> executeOnServerRunning(final NoticeMapper noticeMapper) throws IOException {
        Document document = connectNoticeUrl(noticeMapper);

        // Start Announcement Section Scan
        CompletableFuture<Void> asyncAnnouncementCrawlTask = CompletableFuture.runAsync(() -> {
            try {
                extractNewAnnouncements(document, noticeMapper);
            } catch (IOException e) {
                log.error("IOException occurred while processing {} on thread {}: {}",
                        noticeMapper.getCategory(),
                        Thread.currentThread().getName(),
                        e.getMessage(), e);

                throw new RuntimeException("IOException occurred in "
                        .concat(noticeMapper.getCategory())
                        .concat(" from thread ")
                        .concat(Thread.currentThread().getName()), e);
            }
        });

        // Start Notice Section Scan
        CompletableFuture<Void> asyncNoticeCrawlTask = CompletableFuture.runAsync(() -> {
            try {
                extractNewNotices(document, noticeMapper);
            } catch (IOException e) {
                log.error("IOException occurred while processing {} on thread {}: {}",
                        noticeMapper.getCategory(),
                        Thread.currentThread().getName(),
                        e.getMessage(), e);

                throw new RuntimeException("IOException occurred in "
                        .concat(noticeMapper.getCategory())
                        .concat(" from thread ")
                        .concat(Thread.currentThread().getName()), e);
            }
        });

        // Execute last task when each thread task completed
        CompletableFuture<List<NoticeDto>> afterTask = CompletableFuture.allOf(asyncAnnouncementCrawlTask, asyncNoticeCrawlTask)
                .thenApply(v -> {
                    Set<NoticeDto> noticeSet = newNoticeDtoTable.get(noticeMapper);
                    if (noticeSet == null) {
                        log.warn("{} No new notices found.", noticeMapper);
                        return Collections.emptyList();
                    }

                    List<NoticeDto> resultList = new ArrayList<>(noticeSet);
                    newNoticeDtoTable.forEach((key, value) -> value.clear());
                    return resultList;
                });
        return afterTask.join();
    }

    // [공지] 추출.
    private void extractNewAnnouncements(final Document document, final NoticeMapper noticeMapper) throws IOException {

        // 첫 번째 tr을 가지고 오지 못한 경우
        Element firstContent = extractFirstContent(document, noticeMapper, true);

        // 'N' 이미지가 없는경우
        if (!checkImageTag(firstContent, true)) {
            String errorMessage = "Not exist 'N' image";
            log.error("{} on thread {}: {}", noticeMapper, Thread.currentThread().getName(), errorMessage);
            return;
        }

        // 첫 번째 공지의 nttId를 추출.
        Optional<Long> newFirstNttId = extractAnnouncementNttId(firstContent);

        newFirstNttId.ifPresentOrElse(
                firstNttId -> {
                    // 첫 번째 공지의 nttId가 존재하는 경우
                    Long beforeMaxNttId = noticeAndAnnouncementIdTable.get(noticeMapper).get(0);
                    log.info("NoticeType : {} BeforeMaxNoticeNttId : {}", noticeMapper, beforeMaxNttId);

                    // 이존 nttId가 null 아니면서, 이전 nttId 와 firstNttId가 달라야하고, 이전 nttId 보다 새로운 게시글의 nttId가 더 큰 경우
                    if (beforeMaxNttId != null && (!beforeMaxNttId.equals(firstNttId) && beforeMaxNttId < firstNttId)) {

                        Set<NoticeDto> newNoticeDtoSet = newNoticeDtoTable.computeIfAbsent(noticeMapper, k -> new HashSet<>());
                        Optional<NoticeDto> newFirstAnnounceDto = createNoticeDto(noticeMapper, firstContent, firstNttId, true);

                        newFirstAnnounceDto.ifPresent(newNoticeDtoSet::add);

                        Elements announcements = document.select("tbody > tr:nth-of-type(n+2)");

                        for (Element element : announcements) {
                            // 새로운 게시글의 'N' 이미지 표시가 있는지 확인한다.
                            boolean hasNoticeBul = element.select("td > span.notice_bul").isEmpty();

                            // '[공지]' 표시가 있지 않거나, 이미지 태그가 존재하지 않는 경우 종료한다.
                            if (hasNoticeBul || !checkImageTag(element, true)) {
                                break;
                            }

                            // nttId 를 추출한 결과 beforeMaxNttId 보다 작다면 종료한다.
                            Optional<Long> newAnnouncementNttId = extractAnnouncementNttId(element);
                            if(newAnnouncementNttId.isPresent()) {
                                Long newNttId = newAnnouncementNttId.get();
                                // 두 번째 게시글 부터 추출한 nttId 가 beforeMaxNttId 보다 큰 경우에만, 새로운 게시글로 판단한다.
                                if(newNttId > beforeMaxNttId) {
                                    log.info("New Announcement NttId is greater than beforeMaxNttId : {} > {}", newNttId, beforeMaxNttId);
                                    Optional<NoticeDto> newAnnouncementDto = createNoticeDto(noticeMapper, element, newNttId, true);
                                    newAnnouncementDto.ifPresent(newNoticeDtoSet::add);
                                } else {
                                    // 두 번째 게시글 부터 추출한 nttId 가 beforeMaxNttId 보다 작거나 같은 경우, 이전 게시글로 판단한다.
                                    log.error("New Announcement NttId is lower than beforeMaxNttId : {} < {}", newNttId, beforeMaxNttId);
                                    break;
                                }
                            }
                        }
                        noticeAndAnnouncementIdTable.get(noticeMapper).set(0, firstNttId);
                    }
                },
                () -> {
                    // 첫 번째 공지의 nttId가 존재하지 않는 경우
                    String errorMessage = "No first announcement nttId found in the document";
                    log.error("{} on thread {}: {}", noticeMapper, Thread.currentThread().getName(), errorMessage);
                    throw new NoSuchElementException(errorMessage);
                }
        );


    }

    // [게시글] 추출.
    private void extractNewNotices(final Document document, final NoticeMapper noticeMapper) throws IOException {
        Element firstContent = extractFirstContent(document, noticeMapper, false);

        // 'N' 이미지가 없는경우
        if (!checkImageTag(firstContent, false)) {
            String errorMessage = "Not exist 'N' image";
            log.error("{} on thread {}: {}", noticeMapper, Thread.currentThread().getName(), errorMessage);
            return;
        }

        Optional<Long> firstNoticeNttId = extractNoticeNttId(firstContent);

        firstNoticeNttId.ifPresentOrElse(
                firstNttId -> {
                    // 첫 번째 공지의 nttId가 존재하는 경우
                    Long beforeMaxNttId = noticeAndAnnouncementIdTable.get(noticeMapper).get(1);
                    log.info("NoticeType : {} BeforeMaxAnnouncementNttId : {}", noticeMapper, beforeMaxNttId);
                    // 이존 nttId가 null 아니면서, 이전 nttId 와 firstNttId가 달라야하고, 이전 nttId 보다 새로운 게시글의 nttId가 더 큰 경우
                    if (beforeMaxNttId != null && (!beforeMaxNttId.equals(firstNttId) && beforeMaxNttId < firstNttId)) {

                        Set<NoticeDto> newNoticeDtoSet = newNoticeDtoTable.computeIfAbsent(noticeMapper, k -> new HashSet<>());
                        Optional<NoticeDto> newFirstNoticeDto = createNoticeDto(noticeMapper, firstContent, firstNttId, false);

                        newFirstNoticeDto.ifPresent(newNoticeDtoSet::add);

                        Element nextNotice = firstContent.nextElementSibling();

                        byte checkCount = 1;
                        while (nextNotice != null && checkCount <= 9) {
                            // 요소에 'N' 이미지 표시가 없다면 종료
                            if(!checkImageTag(nextNotice, false)) {
                                break;
                            }
                            // 두 번째 게시글 부터 추출한 nttId 가 beforeMaxNttId 보다 큰 경우에만, 새로운 게시글로 판단한다.
                            Optional<Long> newNoticeNttId = extractNoticeNttId(nextNotice);
                            if(newNoticeNttId.isPresent()) {
                                Long newNttId = newNoticeNttId.get();
                                // 두 번째 게시글 부터 추출한 nttId 가 beforeMaxNttId 보다 큰 경우에만, 새로운 게시글로 판단한다.
                                if(newNttId > beforeMaxNttId) {
                                    log.info("New Notice NttId is greater than beforeMaxNttId : {} > {}", newNttId, beforeMaxNttId);
                                    Optional<NoticeDto> newNoticeDto = createNoticeDto(noticeMapper, nextNotice, newNttId, false);
                                    newNoticeDto.ifPresent(newNoticeDtoSet::add);
                                } else {
                                    // 두 번째 게시글 부터 추출한 nttId 가 beforeMaxNttId 보다 작거나 같은 경우, 이전 게시글로 판단한다.
                                    log.error("New Notice NttId is lower than beforeMaxNttId : {} < {}", newNttId, beforeMaxNttId);
                                    break;
                                }
                            }
                            // 요소에 'N' 이미지 표시가 있다면 다음 요소를 탐색
                            nextNotice = nextNotice.nextElementSibling();
                            checkCount++;
                        }

                        // notice 이므로, notice nttId를 변경.
                        noticeAndAnnouncementIdTable.get(noticeMapper).set(1, firstNttId);
                    }

                },
                () -> {
                    // 첫 번째 게시글의 nttId가 존재하지 않는 경우
                    String errorMessage = "No first notice nttId found in the document";
                    log.error("{} on thread {}: {}", noticeMapper, Thread.currentThread().getName(), errorMessage);
                    throw new NoSuchElementException(errorMessage);
                }
        );
    }

    // 첫 번째 공지 및 게시글 추출.
    private Element extractFirstContent(final Document document, final NoticeMapper noticeMapper, final boolean isAnnouncement) {

        Element firstContent = isAnnouncement ? document.select("tbody > tr").first()
                : document.select("tr:has(td.problem_number):not(:has(span.notice_bul))").first();

        if(firstContent == null) {
            String errorMessage = "No announcement content found in the document";
            log.error("{} on thread {}: {}", noticeMapper, Thread.currentThread().getName(), errorMessage);
            throw new NoSuchElementException(errorMessage);
        }
        return firstContent;
    }

    // 공지 NttId 추출.
    private Optional<Long> extractAnnouncementNttId(final Element targetElement) {
        String targetString = targetElement.html();
        String[] lines = targetString.split("\n");

        for (String line : lines) {
            if (line.contains("nttId=")) {
                int nttIdIndex = line.indexOf("nttId=");
                if (nttIdIndex != -1) {
                    String nttIdSubString = line.substring(nttIdIndex + 6);
                    int endIndex = nttIdSubString.indexOf("&");

                    if (endIndex != -1) {
                        String nttId = nttIdSubString.substring(0, endIndex);
                        try {
                            return Optional.of(Long.parseLong(nttId));
                        } catch (NumberFormatException e) {
                            log.warn("Failed to parse nttId: {}", nttId, e);
                            return Optional.empty();
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    // 게시글 NttId 추출.
    private Optional<Long> extractNoticeNttId(final Element targetElement) {
        return Optional.of(Long.parseLong(targetElement.select("input[type=hidden][name=nttId]").val()));
    }

    // 새로운 게시글에 대한 NoticeDto 생성.
    private Optional<NoticeDto> createNoticeDto(final NoticeMapper noticeMapper, final Element content, final Long nttId, final boolean isAnnouncement) {
        try {
            String title = isAnnouncement ?
                    content.select("td.left > div > span > a > b").text() :
                    content.select("td.left > div > div > form > input[type=submit]").val();

            int contentNumber = isAnnouncement ? -1 : Integer.parseInt(content.select("td.problem_number").text());
            String departName = isAnnouncement ?
                    content.select("td.problem_name > b").text() :
                    content.select("td.problem_name").text();

            String registeredAt = isAnnouncement ?
                    content.select("td.date > b").text() :
                    content.select("td.date").get(0).text();

            String contentUrl = noticeMapper.getInternalContentUrl().concat("&nttId=").concat(String.valueOf(nttId));

            Document articleDocument = Jsoup.connect(contentUrl).get();

            Elements articleContent = articleDocument.select("div.bbs_detail_content");
            String contentImage = articleContent.select("div.bbs_detail_content img").attr("src");

            contentImage = adjustImageContent(contentImage);

            NoticeDto noticeDto = NoticeDto.builder()
                    .noticeMapper(noticeMapper)
                    .nttId(nttId)
                    .title(title)
                    .contentUrl(contentUrl)
                    .contentImage(contentImage)
                    .departName(departName)
                    .contentNumber(contentNumber)
                    .registeredAt(registeredAt)
                    .build();

            return Optional.of(noticeDto);

        } catch (Exception e) {
            log.error("Error creating NoticeDto: ", e);
            return Optional.empty();
        }
    }

    private String adjustImageContent(String contentImage) {
        if (contentImage.isEmpty()) {
            return null;
        }
        return contentImage.startsWith("https:") ? contentImage
            : "https" + contentImage.substring(4);
    }

    private boolean checkImageTag(Element content, boolean isAnnouncement) {
        Elements imageTags = isAnnouncement ? content.select("td.left > div.list_subject > span.link > a > img")
                : content.select("td.left > div.list_subject > div.link > form > img");
        return !imageTags.isEmpty();
    }


}
