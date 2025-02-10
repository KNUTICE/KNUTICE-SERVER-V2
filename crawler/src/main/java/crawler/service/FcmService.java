package crawler.service;

import com.google.firebase.messaging.*;
import crawler.fcmutils.FcmUtils;
import crawler.fcmutils.MessageWithFcmToken;
import crawler.service.model.FcmDto;
import db.domain.token.fcm.FcmTokenDocument;
import db.domain.token.fcm.FcmTokenMongoRepository;
import global.utils.NoticeMapper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
    private static final int MAX_RETRIES = 4; // 최대 재시도 횟수
    private static final int BASE_BACKOFF_TIME = 1; // 기본 대기 시간 1s
    private static final int MAX_BACKOFF_TIME = BASE_BACKOFF_TIME * (1 << (MAX_RETRIES - 1)); // 최대 대기 시간 8s
    // unmodifiableSet 을 활용해서, 외부에서 enum 구조를 수정하지 못하게 방지. ( Set 집합내 요소들을 수정되지 못하도록 설정 )
    private static final Set<MessagingErrorCode> RETRYABLE_ERROR_CODES =
            Collections.unmodifiableSet(EnumSet.of(
                    MessagingErrorCode.INTERNAL,
                    MessagingErrorCode.UNAVAILABLE,
                    MessagingErrorCode.QUOTA_EXCEEDED
            ));

    private void sendToTopic(FcmDto fcmDto, NoticeMapper noticeMapper) throws FirebaseMessagingException {
        List<FcmTokenDocument> fcmTokenDocumentList = getActivateTopicListBy(noticeMapper);

        List<String> deviceTokenList = fcmTokenDocumentList.stream()
            .map(FcmTokenDocument::getFcmToken)
            .toList();

        batchSend(fcmDto, deviceTokenList);
    }

    public void batchSend(FcmDto fcmDto, List<String> deviceTokenList) {
        List<MessageWithFcmToken> messageWithTokenList = new ArrayList<>();

        // 특정 공지를 구독한 사람이 없는 경우. 전송할 이유가 없음.
        if(deviceTokenList.size() == 0) {
            log.warn("해당 주제를 구독한 사용자가 없습니다.");
            return;
        }

        // 각 FCM 토큰에 대해 Message 생성
        for (String token : deviceTokenList) {
            Message message = fcmUtils.createMessageBuilder(fcmDto, token);
            messageWithTokenList.add(new MessageWithFcmToken(message, token));
        }


        /**
         * 최대 500개 전송이 가능하므로, 토큰 집합을 T 라 했을때, 1 <= T <= ⌈ specificNoticeSubscriber / 500 ⌉
         * 토큰 집합이 적어도 1개 이상존재, 500개 이하인 경우, 반드시 하나의 토큰집합을 생성.
         * 500개가 초과하면 반드시 토큰 집합은 두 개 이상 생성.
         *
         *
         * 스레드 개수 결정 로직:
         * T <= 500: 스레드 1개 사용 - 동기방식
         * T > 500: (T / 500)을 올림(ceil)하여 해당 개수만큼 스레드 생성 - 병렬처리
         *
         */
        int tokenSize = (int) Math.ceil((double) messageWithTokenList.size() / 500);


        if (tokenSize == 1) {
            processBatch(messageWithTokenList, 0);
        } else {
            // forkjoinpool 에 thread 가 1개 이상 default 로 존재한다고 가정.
            // 만약 forkjoinpool 에 thread 가 1개로만 유지하는 경우, 병렬성이 보장안됨.
            List<CompletableFuture<Void>> futureList = new ArrayList<>();
            // 토큰 집합의 개수만큼.
            for (int i = 0; i < tokenSize; i++) {
                int start = i * MAX_DEVICES_PER_MESSAGE;
                int end = Math.min((i + 1) * MAX_DEVICES_PER_MESSAGE, messageWithTokenList.size());
                List<MessageWithFcmToken> subList = messageWithTokenList.subList(start, end);

                // 각 토큰에 대해서 비동기로 수행시키기 위해서 futureList 에 저장.
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> processBatch(subList, 0));
                futureList.add(future);
            }
            // 모든 작업을 완료하기 까지 대기한 후 종료
                // allOf 가 sequence 타입으로 받고 있음. 즉 가변인자로 파라미터를 받고 있기 때문에
                // List 타입을, 배열 타입으로 변환해주는게 필요.
                // List.toArray() 에서 new Comle..[0] 으로 설정한 이유는, 본 메소드는 내부적으로, 전달된 배열의 값이 작은 경우
                // futureList 에 맞게 배열을 생성해줌.

            /**
             * public <T> T[] toArray(T[] a) {
             *         if (a.length < size)
             *             // Make a new array of a's runtime type, but my contents:
             *             return (T[]) Arrays.copyOf(elementData, size, a.getClass());
             *         System.arraycopy(elementData, 0, a, 0, size);
             *         if (a.length > size)
             *             a[size] = null;
             *         return a;
             *     }
             *
             *     size == List size
             *     작업을 반드시 등록하므로, 적어도 1개이상이고, 0을 넘기면, 반드시 작은 경우만 생기므로, 자바 내부 메소드의 최적화를 이용함.
             */
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        }
    }




    // 토큰 집합이 1개인 경우
    private void processBatch(List<MessageWithFcmToken> targetList, int retryCount) {

        // 재귀 베이스 조건.
        if (retryCount > MAX_RETRIES) {
            log.error("최대 재시도 횟수가 초과되었습니다.");
            return;
        }

        List<Message> messageList = targetList.stream()
                .map(MessageWithFcmToken::getMessage)
                .collect(Collectors.toList());

        BatchResponse batchResponse = null;
        int attempt = 1;

        while (batchResponse == null && attempt <= MAX_RETRIES) {
            try {
                batchResponse = FirebaseMessaging.getInstance().sendEach(messageList);
                log.info("전송 개수: {}", batchResponse.getResponses().size());
            } catch (FirebaseMessagingException e) {
                MessagingErrorCode errorCode = e.getMessagingErrorCode();
                if (errorCode == MessagingErrorCode.THIRD_PARTY_AUTH_ERROR
                        || errorCode == MessagingErrorCode.INVALID_ARGUMENT
                        || errorCode == MessagingErrorCode.SENDER_ID_MISMATCH) {
                    log.error("메세지를 전송할 수 없는 상태입니다. 재시도 불가.");
                    return;
                }
                backOff(attempt++);
            }
        }

        if (batchResponse.getFailureCount() == 0) {
            log.info("FCM 전송 성공 (총 {}개)", batchResponse.getSuccessCount());
            return;
        }

        List<MessageWithFcmToken> failedMessages = new ArrayList<>();
        List<String> deleteTokenList = new ArrayList<>();

        List<SendResponse> sendResponseList = batchResponse.getResponses();
        for (int i = 0; i < sendResponseList.size(); i++) {
            if (!sendResponseList.get(i).isSuccessful()) {
                MessagingErrorCode errorCode = sendResponseList.get(i).getException().getMessagingErrorCode();
                if (RETRYABLE_ERROR_CODES.contains(errorCode)) {
                    failedMessages.add(targetList.get(i));
                } else if (errorCode == MessagingErrorCode.UNREGISTERED) {
                    deleteTokenList.add(targetList.get(i).getFcmToken());
                }
            }
        }

        // 삭제 토큰 작업
        CompletableFuture<Void> deleteTokenTask = CompletableFuture.runAsync(() -> {
            if (!deleteTokenList.isEmpty()) {
                List<FcmTokenDocument> tokenDocumentList = fcmTokenMongoRepository.findAllById(deleteTokenList);
                fcmUtils.deleteTokens(tokenDocumentList);
            }
        });

        // 재시도 작업, 재전송에 실패하는 메세지가 없을때까지
        CompletableFuture<Void> retryTokenTask = CompletableFuture.runAsync(() -> {
            if (!failedMessages.isEmpty()) {
                // 재귀를 타고 종료되면, 재귀 깊이 만큼 processBatch 가 종료되고, 이후에 join() 작업으로 넘어가기 때문에
                // 최초 batchSend 메소드의 종료시점은, 모든 재귀가 끝나는 시점.
                processBatch(failedMessages, retryCount + 1);
            }
        });

        // 두 비동기 작업이 모두 완료될 때까지 기다린 후 종료.
        CompletableFuture.allOf(deleteTokenTask, retryTokenTask).join();
    }

    private void backOff(int attempt) {
        // 2의 거듭제곱으로 떨어지므로
        int waitTime = Math.min(BASE_BACKOFF_TIME * (1 << (attempt - 1)), MAX_BACKOFF_TIME);
        try {
            TimeUnit.SECONDS.sleep(waitTime); // 1초 동안 대기
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("재시도 대기 중 인터럽트 발생", ie);
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