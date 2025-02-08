package crawler.service;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import crawler.fcmutils.FcmUtils;
import crawler.fcmutils.MessageWithFcmToken;
import crawler.service.model.FcmDto;
import db.domain.token.fcm.FcmTokenDocument;
import db.domain.token.fcm.FcmTokenMongoRepository;
import global.utils.NoticeMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenMongoRepository fcmTokenMongoRepository;
    private final FcmUtils fcmUtils;

    private static final int MAX_DEVICES_PER_MESSAGE = 500;
    private static final int MAX_RETRIES = 3; // 최대 재시도 횟수

    private void sendToTopic(FcmDto fcmDto, NoticeMapper noticeMapper) throws FirebaseMessagingException {
        List<FcmTokenDocument> fcmTokenDocumentList = getActivateTopicListBy(noticeMapper);

        List<String> deviceTokenList = fcmTokenDocumentList.stream()
            .map(FcmTokenDocument::getFcmToken)
            .toList();

        sendBatch(fcmDto, deviceTokenList);
    }

    /**
     * FCM 알림을 배치 단위로 전송 및 실패한 메시지 재시도
     * 첫 번째 전송(시도) - 바로 전송
     * 두 번째 시도 - 1000ms 대기 후 전송
     * 세 번째 시도 - 4000ms 대기 후 전송
     */

    public void sendBatch(FcmDto fcmDto, List<String> deviceTokenList) {
        List<MessageWithFcmToken> messageWithTokenList = fcmUtils.createMessagesWithTokenList(fcmDto, deviceTokenList);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < messageWithTokenList.size(); i += MAX_DEVICES_PER_MESSAGE) {
            List<MessageWithFcmToken> batchList = new ArrayList<>(messageWithTokenList.subList(i, Math.min(i + MAX_DEVICES_PER_MESSAGE, messageWithTokenList.size())));

            // 각 배치 처리를 비동기로 실행
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> processBatch(batchList));
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processBatch(List<MessageWithFcmToken> batchList) {
        int attempt = 0;
        log.info("FCM 전송 실행 중인 스레드 : {}", Thread.currentThread().getName());

        while (attempt < MAX_RETRIES && !batchList.isEmpty()) {
            List<Message> messageList = batchList.stream()
                .map(MessageWithFcmToken::getMessage)
                .collect(Collectors.toList());

            BatchResponse batchResponse;
            try {
                batchResponse = FirebaseMessaging.getInstance().sendEach(messageList);
                log.info("[{}] 전송 개수: {}", Thread.currentThread().getName(), batchResponse.getResponses().size());
            } catch (FirebaseMessagingException e) {
                log.error("[{}] FCM 요청 실패 (시도: {}/{}): {}",Thread.currentThread().getName(), attempt + 1, MAX_RETRIES, e.getMessage());
                attempt++;
                backoff(attempt);
                continue;
            }

            int failureCount = batchResponse.getFailureCount();
            if (failureCount == 0) {
                log.info("[{}] FCM 전송 성공 (총 {}개)", Thread.currentThread().getName(), batchResponse.getSuccessCount());
                return;
            }

            log.warn("[{}] FCM 일부 실패: 성공 {}개, 실패 {}개 (시도: {}/{})", Thread.currentThread().getName(), batchResponse.getSuccessCount(), failureCount, attempt + 1, MAX_RETRIES);

            batchList = fcmUtils.filterFailedMessages(batchList, batchResponse);
            attempt++;

            if (attempt != MAX_RETRIES) {
                backoff(attempt);
            }
        }

        if (!batchList.isEmpty()) {
            log.error("[{}] FCM 전송 실패: 최대 재시도 횟수 초과", Thread.currentThread().getName());

            List<String> failedTokenList = batchList.stream()
                .map(MessageWithFcmToken::getFcmToken)
                .collect(Collectors.toList());

            fcmUtils.updateFailedToken(failedTokenList);
        }
    }

    /**
     * 지수 백오프(Exponential Backoff) 적용하여 재시도 대기
     */
    private void backoff(int attempt) {
        int backoffTime = (int) (Math.pow(2, attempt) * 1000);
        log.info("[{}] 백오프 적용 (시도 {}): {}ms 동안 대기", Thread.currentThread().getName(), attempt, backoffTime);
        try {
            Thread.sleep(backoffTime);
        } catch (InterruptedException ie) {
            log.error("[{}] 백오프 중 인터럽트 발생", Thread.currentThread().getName(), ie);
            throw new IllegalStateException("백오프 중 인터럽트 발생", ie);
        }
    }

    public void fcmTrigger(List<String> noticeTitleList, NoticeMapper noticeMapper) throws FirebaseMessagingException {
        String category = "[" + noticeMapper.getCategory() + "]";

        noticeTitleList.forEach(title -> log.info("새로운 공지 - {} : {}", category, title));

        if (noticeTitleList.size() >= 3) {
            sendToTopic(fcmUtils.buildMultipleNotice(noticeTitleList, category), noticeMapper);
        } else {
            noticeTitleList.forEach(title -> {
                FcmDto fcmDto = fcmUtils.buildSingleNotice(category, title);
                try {
                    sendToTopic(fcmDto, noticeMapper);
                } catch (FirebaseMessagingException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private List<FcmTokenDocument> getActivateTopicListBy(NoticeMapper noticeMapper) {
        return switch (noticeMapper) {
            case GENERAL_NEWS -> fcmTokenMongoRepository.findAllByGeneralNewsTopicTrue();
            case SCHOLARSHIP_NEWS -> fcmTokenMongoRepository.findAllByScholarshipNewsTopicTrue();
            case EVENT_NEWS -> fcmTokenMongoRepository.findAllByEventNewsTopicTrue();
            case ACADEMIC_NEWS -> fcmTokenMongoRepository.findAllByAcademicNewsTopicTrue();
        };
    }
}