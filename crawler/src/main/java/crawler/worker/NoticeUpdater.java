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

    public void updateNoticeTitles (List<NoticeDto> titles, NoticeMapper noticeMapper, final boolean boot)
            throws FirebaseMessagingException {

        boolean isNotUpdated = noticeProcessor.noticeUpdate(titles, noticeMapper, boot);

        if(!boot && !isNotUpdated) {
            log.info("Not initial start and updated new notices");
            Optional<List<String>> newNoticeTitleList = noticeProcessor.getUpdateNoticeMap(noticeMapper);
            newNoticeTitleList.ifPresent(newNoticeTitles -> {
                log.info("updateNoticeTitles check lists size : {}", newNoticeTitles.size());
                try {
                    fcmService.fcmTrigger(newNoticeTitles, noticeMapper);
                } catch (FirebaseMessagingException e) {
                    log.error("FirebaseMessaging Exception e");
                    throw new RuntimeException(e);
                }
            });
        }
    }
}

