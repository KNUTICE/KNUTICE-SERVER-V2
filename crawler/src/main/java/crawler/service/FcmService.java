package crawler.service;

import com.google.firebase.messaging.*;
import crawler.fcmutils.FcmMessageFilter;
import crawler.fcmutils.FcmUtils;
import crawler.fcmutils.MessageWithFcmToken;
import crawler.fcmutils.RetryableErrorCode;
import crawler.service.model.FcmDto;
import db.domain.token.fcm.FcmTokenDocument;
import global.utils.NoticeMapper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmService {

    private final FcmMessageFilter fcmMessageFilter;
    private final FcmUtils fcmUtils;

    private static final int MAX_DEVICES_PER_MESSAGE = 500;
    private static final int MAX_RETRIES = 4; // 최대 재시도 횟수
    private static final int BASE_BACKOFF_TIME = 1; // 기본 대기 시간 1s
    private static final int MAX_BACKOFF_TIME = BASE_BACKOFF_TIME * (1 << MAX_RETRIES - 1); // 최대 대기 시간 8s

    private void sendToTopic(FcmDto fcmDto, NoticeMapper noticeMapper)
        throws FirebaseMessagingException {
        List<FcmTokenDocument> fcmTokenDocumentList = fcmUtils.getActivateTopicListBy(noticeMapper);

        List<String> deviceTokenList = fcmTokenDocumentList.stream()
            .map(FcmTokenDocument::getFcmToken)
            .toList();

        batchSend(fcmDto, deviceTokenList);
    }

    public void batchSend(FcmDto fcmDto, List<String> deviceTokenList) {

        if (deviceTokenList.size() == 0) {
            log.warn("해당 주제를 구독한 사용자가 없습니다.");
            return;
        }

        List<MessageWithFcmToken> messageWithTokenList = fcmUtils.createMessagesWithTokenList(fcmDto, deviceTokenList);

        int tokenSize = (int) Math.ceil((double) messageWithTokenList.size() / 500);

        if (tokenSize == 1) {
            processBatch(messageWithTokenList, 0);
        } else {
            log.info("배치 처리 병렬 실행 시작 (토큰 집합 수: {})", tokenSize);
            List<CompletableFuture<Void>> futureList = new ArrayList<>();
            for (int i = 0; i < tokenSize; i++) {
                int start = i * MAX_DEVICES_PER_MESSAGE;
                int end = Math.min((i + 1) * MAX_DEVICES_PER_MESSAGE, messageWithTokenList.size());
                List<MessageWithFcmToken> subList = messageWithTokenList.subList(start, end);

                CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> processBatch(subList, 0));
                futureList.add(future);
            }
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        }
    }

    // 토큰 집합이 1개인 경우
    public void processBatch(List<MessageWithFcmToken> targetList, int retryCount) {

        List<Message> messageList = targetList.stream()
            .map(MessageWithFcmToken::getMessage)
            .collect(Collectors.toList());

        BatchResponse batchResponse = null;
        int attempt = 1;

        while (batchResponse == null && attempt <= MAX_RETRIES) {

            log.info("전체시도: {}/{}", attempt, MAX_RETRIES);

            try {
                /**
                 * sendEach 메서드는 여러 메시지를 한 번에 보내는 역할을 합니다.
                 * 전체 요청 실패: 네트워크 문제, Firebase 서버 내부 오류, 인증 문제 등으로 인해 전체 요청이 실패하면 FirebaseMessagingException이 발생할 수 있습니다.
                 * sendEach 메서드는 개별 메세지에 대한 오류를 잡는게 아닌, 전체 요청에 대한 예외를 캐치합니다. 개별 메시지 에러에 대한 사항은 batchResponse 에 담겨 개별 오류로 처리가 가능합니다.
                 */
                batchResponse = FirebaseMessaging.getInstance().sendEach(messageList);
            } catch (FirebaseMessagingException e) {
                /**
                 * 전체 배치 메세지 전송에 대해서 전송이 불가능한 경우 예외 발생에 대한 처리.
                 */


                /**
                 * @com.google.firebase.internal.Nullable
                 * public com.google.firebase.messaging.MessagingErrorCode getMessagingErrorCode()
                 *
                 *
                 * 널 발생이 가능하기 때문에, null 에 대한 처리가 필요, null 을 반환하는 경우, 예외 처리가 불가능함.
                 */
                MessagingErrorCode errorCode = e.getMessagingErrorCode();

                if (errorCode == null) {
                    log.error("에러 코드가 없는 FirebaseMessagingException 발생: {}", e.getMessage());
                    return;
                }

                /**
                 * Description.
                 * MessagingErrorCode.THIRD_PARTY_AUTH_ERROR -> 외부 인증 시스템에 의한 예외
                 * MessagingErrorCode.INVALID_ARGUMENT, -> 형식이 잘못된 경우
                 * MessagingErrorCode.SENDER_ID_MISMATCH, -> fcm 에서 설정된 sender id 와 발신자의 id 가 다른 경우
                 */

                // 재시도 불가능한 오류 처리
                if (RetryableErrorCode.NOT_RETRYABLE.contains(errorCode)) {
                    log.error("메세지를 전송할 수 없는 상태입니다 (재시도 불가). 에러 코드: {}", errorCode);
                    return;
                } else if (RetryableErrorCode.RETRYABLE.contains(errorCode)) {
                    // 재시도가 가능한 오류 처리
                    log.warn("일시적인 오류 발생, 재시도 가능: {} - 시도 횟수: {}", errorCode, attempt);
                    attempt = attempt + 1;
                    backOff(attempt);  // 백오프 후 재시도
                    continue;
                }

                // 그 외 예상치 못한 오류는 로깅만 하고 종료
                log.error("예상치 못한 오류가 발생했습니다: {} - {}번 시도", errorCode, attempt);
                return;
            }
        }

        // 전체 전송 이후, backoff 최대 재시도 횟수가 넘어서 while 문이 종료된 경우
        if (batchResponse == null) {
            log.error("최대 재전송 횟수 시도 이후, 전송에 실패한 경우 종료");
            return;
        }
        // 전체 전송 이후, 전송에 성공했고, 전송된 값들중에서 실패한 토큰이 없는 경우에는 재시도 로직 및 삭제 로직을 적용할 필요가 없음.
        if (batchResponse != null && batchResponse.getFailureCount() == 0) {
            log.info("FCM 전송 모두 성공 (총 {}개)", batchResponse.getSuccessCount());
            return;
        }

        List<SendResponse> sendResponseList = batchResponse.getResponses();

        List<MessageWithFcmToken> failedMessages = fcmMessageFilter.filterFailedMessage(
                sendResponseList, targetList);

        // 재귀 베이스 조건. (삭제 x)
        if (retryCount + 1 >= MAX_RETRIES) {
            log.error("최대 재시도 횟수가 초과되었습니다.");
            return;
        }

        backOff(retryCount + 1);

        // 재시도 작업, 재전송에 실패하는 메세지가 없을때까지
        CompletableFuture<Void> retryTokenTask = CompletableFuture.runAsync(() -> {
            if (!failedMessages.isEmpty()) {
                log.info("재시도할 메시지 개수: {}", failedMessages.size());
                processBatch(failedMessages, retryCount + 1);
            }
        });

        CompletableFuture.allOf(retryTokenTask).join();

    }

    private void backOff(int attempt) {
        int waitTime = Math.min(BASE_BACKOFF_TIME * (1 << (attempt - 1)), MAX_BACKOFF_TIME);
        try {
            log.info("백오프 적용 (시도 {}): {}s 동안 대기", attempt, waitTime);
            TimeUnit.SECONDS.sleep(waitTime); // 1초 동안 대기
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("재시도 대기 중 인터럽트 발생", ie);
        }
    }

    public void fcmTrigger(List<String> noticeTitleList, NoticeMapper noticeMapper)
        throws FirebaseMessagingException {
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

}
