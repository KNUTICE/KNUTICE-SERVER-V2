package crawler.service;

import com.google.firebase.messaging.*;
import crawler.fcmutils.FcmMessageFilter;
import crawler.fcmutils.FcmTokenManager;
import crawler.fcmutils.FcmDtoBuilder;
import crawler.fcmutils.FcmMessageGenerator;
import crawler.fcmutils.dto.FilteredFcmResult;
import crawler.fcmutils.dto.MessageWithFcmToken;
import crawler.fcmutils.RetryableErrorCode;
import crawler.service.model.FcmDto;
import crawler.worker.model.NoticeDto;
import db.domain.token.fcm.FcmTokenDocument;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmService {

    // DEPENDENCY
    private final FcmMessageFilter fcmMessageFilter;
    private final FcmMessageGenerator fcmMessageGenerator;
    private final FcmTokenManager fcmTokenManager;
    private final FcmDtoBuilder fcmDtoBuilder;


    // CONSTANT
    private static final int MAX_DEVICES_PER_MESSAGE = 500;
    private static final int MAX_RETRIES = 4; // 최대 재시도 횟수
    private static final int BASE_BACKOFF_TIME = 1; // 기본 대기 시간 1s
    private static final int MAX_BACKOFF_TIME = BASE_BACKOFF_TIME * (1 << MAX_RETRIES - 1); // 최대 대기 시간 8s
    private static final int BASE_RETRY_COUNT = 0;


    // CLASS VARIABLE
    private final List<String> tokenListToDelete = new ArrayList<>(); // 최대 재시도 횟수 후 삭제할 토큰 모음
    private final List<String> failedTokenListToUpdate = new ArrayList<>(); // 최대 재시도 횟수 후 실패한 토큰 모음

    private void sendToTopic(FcmDto fcmDto)
        throws FirebaseMessagingException {
        List<FcmTokenDocument> fcmTokenDocumentList = fcmTokenManager.getActivateTopicListBy(fcmDto.getNoticeName());

        List<String> deviceTokenList = fcmTokenDocumentList.stream()
            .map(FcmTokenDocument::getFcmToken)
            .toList();

        batchSend(fcmDto, deviceTokenList);
    }

    public void batchSend(FcmDto fcmDto, List<String> deviceTokenList) {

        // 1. 비어있다면 종료
        if (deviceTokenList.isEmpty()) {
            log.warn("해당 주제를 구독한 사용자가 없습니다.");
            return;
        }

        // 2. 부모 스레드 이름을 MDC 에 설정
        // 최초 호출 시, 체인에 자신의 이름(현재 작업을 처리하는 Thread name) 만 기록
        MDC.put("parentThread", Thread.currentThread().getName());

        List<MessageWithFcmToken> messageWithTokenList = fcmMessageGenerator.generateMessageBuilderList(
                fcmDto, deviceTokenList);

        int tokenSize = (int) Math.ceil((double) messageWithTokenList.size() / 500);

        if (tokenSize == 1) {
            // 단일 처리면 바로 진행
            processBatch(messageWithTokenList, BASE_RETRY_COUNT);
        } else {
            log.info("배치 처리 병렬 실행 시작 (토큰 집합 수: {})", tokenSize);

            // 부모 MDC 컨텍스트 복사
            Map<String, String> parentMdcContext = MDC.getCopyOfContextMap();
            List<CompletableFuture<Void>> futureList = new ArrayList<>();

            for (int i = 0; i < tokenSize; i++) {
                int start = i * MAX_DEVICES_PER_MESSAGE;
                int end = Math.min((i + 1) * MAX_DEVICES_PER_MESSAGE, messageWithTokenList.size());
                List<MessageWithFcmToken> subList = messageWithTokenList.subList(start, end);

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    // 자식 스레드에서 기존 컨텍스트를 복원
                    if (parentMdcContext != null) {
                        MDC.setContextMap(parentMdcContext);
                    }
                    // 부모 스레드가 지정되지 않았다면
                    // 체인 확장 현재 스레드 이름을 덧붙이기
                    String chain = extendChainLog() + " -> " + Thread.currentThread().getName();
                    MDC.put("parentThread", chain);

                    // 실제 처리
                    processBatch(subList, BASE_RETRY_COUNT);

                    // 스레드 작업 종료 후 MDC 정리
                    MDC.clear();
                });

                futureList.add(future);
            }
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        }

        // 메인 스레드 MDC 초기화
        MDC.clear();
        clearAndManageToken();
        log.info("[ALERT] 배치 작업 종료");
    }

    private static void getWarn() {
        log.warn("부모 스레드가 context 에 저장되지 않음");
    }

    // 토큰 집합이 1개인 경우
    public void processBatch(List<MessageWithFcmToken> targetList, int retryCount) {
        // 이미 병렬로 호출됨.

        List<Message> messageList = targetList.stream()
                .map(MessageWithFcmToken::getMessage)
                .collect(Collectors.toList());

        BatchResponse batchResponse = null;
        int totalSendAttempts = 1;

        // 재귀 호출 로깅
        log.info("[ALERT] 재귀시도(retry) : {}/{}, 부모 스레드 : {}", retryCount + 1, MAX_RETRIES, MDC.get("parentThread"));

        while (batchResponse == null && totalSendAttempts <= MAX_RETRIES) {
            log.info("[ALERT] 전체시도(attempt) : {}/{}, 부모 스레드 : {}", totalSendAttempts, MAX_RETRIES, MDC.get("parentThread"));
            try {
                batchResponse = FirebaseMessaging.getInstance().sendEach(messageList);
                log.info("[ALERT] 전송 개수(실패 포함) : {}, 부모 스레드 : {}", batchResponse.getResponses().size(), MDC.get("parentThread"));
            } catch (FirebaseMessagingException e) {
                MessagingErrorCode errorCode = e.getMessagingErrorCode();

                if (errorCode == null) {
                    log.error("에러 코드가 없는 FirebaseMessagingException 발생: {}, 부모 스레드 : {}", e.getMessage(), MDC.get("parentThread"));
                    return;
                }

                // 재시도 불가 에러
                if (RetryableErrorCode.NOT_RETRYABLE.contains(errorCode)) {
                    log.error("메세지를 전송할 수 없는 상태 (재시도 불가). 에러 코드: {}, 부모 스레드 : {}", errorCode, MDC.get("parentThread"));
                    return;
                } else if (RetryableErrorCode.RETRYABLE.contains(errorCode)) {
                    // 재시도가 가능한 오류
                    log.warn("일시적인 오류 발생, 재시도 가능: {} - 시도 횟수: {}, 부모 스레드 : {}", errorCode, totalSendAttempts, MDC.get("parentThread"));
                    totalSendAttempts++;
                    backOff(totalSendAttempts);
                    continue;
                }

                // 그 외 예외
                log.error("예상치 못한 오류가 발생했습니다: {} - {}번 시도, 부모 스레드 : {}", errorCode, totalSendAttempts, MDC.get("parentThread"));
                return;
            }
        }

        // 전체 전송 실패
        if (batchResponse == null) {
            log.error("최대 재전송 횟수 시도 이후, 전송 실패 (batchResponse null), 부모 스레드 : {}", MDC.get("parentThread"));
            return;
        }

        // 성공/실패 처리
        if (batchResponse.getFailureCount() == 0) {
            log.info("[ALERT] FCM 전송 전체 성공 (총 {}개), 부모 스레드 : {}", batchResponse.getSuccessCount(), MDC.get("parentThread"));
            return;
        }

        // 실패한 결과 필터링
        List<SendResponse> sendResponseList = batchResponse.getResponses();
        FilteredFcmResult filteredFcmResult = fcmMessageFilter.filterFailedMessage(sendResponseList, targetList);

        if (retryCount + 1 >= MAX_RETRIES) {
            log.error("[ALERT] 최대 재시도 횟수가 초과되었습니다. 부모 스레드 : {}", MDC.get("parentThread"));
            for (MessageWithFcmToken token : filteredFcmResult.getFailedMessageList()) {
                failedTokenListToUpdate.add(token.getFcmToken());
            }
            tokenListToDelete.addAll(filteredFcmResult.getDeleteTokenList());
            return;
        }

        // 재시도 백오프
        backOff(retryCount + 1);

        CompletableFuture<Void> retryTokenTask = CompletableFuture.runAsync(() -> {

            // 출력체인 확장
            String chain = extendChainLog() + " -> " + Thread.currentThread().getName();
            MDC.put("parentThread", chain);

            List<MessageWithFcmToken> failedMessageList = filteredFcmResult.getFailedMessageList();
            if (!failedMessageList.isEmpty()) {
                log.info("[ALERT] 재시도 메시지 개수 : {}, 부모 스레드 : {}", failedMessageList.size(), MDC.get("parentThread"));
                processBatch(failedMessageList, retryCount + 1);
            }
            MDC.clear();
        });

        tokenListToDelete.addAll(filteredFcmResult.getDeleteTokenList());

        CompletableFuture.allOf(retryTokenTask).join();
    }

    /**
     * 체인 확장 문구 생성 <br>
     * Author itstime0809
     */
    private static String extendChainLog() {
        String chain = MDC.get("parentThread");
        if (chain == null) {
            // 체인이 정상적으로 저장되지 않았다면 경고성 메세지 출력.
            getWarn();
            chain = "";
        }
        return chain;
    }


    /**
     * 지수 백오프 적용
     */
    private void backOff(int attempt) {
        int waitTime = Math.min(BASE_BACKOFF_TIME * (1 << (attempt - 1)), MAX_BACKOFF_TIME);
        try {
            log.info("[ALERT] 백오프 적용 (시도 {}): {}s 동안 대기", attempt, waitTime);
            TimeUnit.SECONDS.sleep(waitTime); // 1초 동안 대기
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("재시도 대기 중 인터럽트 발생", ie);
        }
    }

    private void clearAndManageToken() {
        fcmTokenManager.manageToken(tokenListToDelete, failedTokenListToUpdate);
        tokenListToDelete.clear();
        failedTokenListToUpdate.clear();
    }

    public void fcmTrigger(List<NoticeDto> noticeDtoList)
        throws FirebaseMessagingException {
        String category = "[" + noticeDtoList.get(0).getNoticeMapper().getCategory() + "]";

        noticeDtoList.forEach(noticeDto -> log.info("[ALERT] 새로운 공지 - {} : {}", category, noticeDto.getTitle()));

        if (noticeDtoList.size() >= 3) {
            sendToTopic(fcmDtoBuilder.createMultipleFcmMessage(noticeDtoList));
        } else {
            noticeDtoList.forEach(noticeDto -> {
                FcmDto fcmDto = fcmDtoBuilder.createSingleFcmMessage(noticeDto);
                try {
                    sendToTopic(fcmDto);
                } catch (FirebaseMessagingException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

}
