package crawler.worker;

import com.google.firebase.messaging.FirebaseMessagingException;
import crawler.service.FcmService;
import crawler.worker.model.NoticeDto;
import global.utils.NoticeMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeUpdater {

    private final NoticeProcessor noticeProcessor;
    private final FcmService fcmService;

    public void updateNoticeTitles (List<NoticeDto> noticeDtoList, NoticeMapper noticeMapper, final boolean boot)
            throws FirebaseMessagingException {

        boolean isNotUpdated = noticeProcessor.noticeUpdate(noticeDtoList, noticeMapper, boot);

        if(!boot && !isNotUpdated) {
            log.info("Not initial start and updated new notices");
            Optional<List<NoticeDto>> newNoticeDtoList = noticeProcessor.getUpdateNoticeMap(noticeMapper);
            newNoticeDtoList.ifPresent(newNoticeDtos -> {
                log.info("updateNoticeTitles check lists size : {}", newNoticeDtos.size());
                try {
                    fcmService.fcmTrigger(newNoticeDtos);
                } catch (FirebaseMessagingException e) {
                    log.error("FirebaseMessaging Exception e");
                    throw new RuntimeException(e);
                }
            });
        }
    }
}

