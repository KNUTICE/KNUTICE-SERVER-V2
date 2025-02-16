package crawler.fcmutils;

import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
import db.domain.token.fcm.FcmTokenMongoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmMessageFilter {

    private final FcmTokenMongoRepository fcmTokenMongoRepository;

    public List<MessageWithFcmToken> filterFailedMessage(
        List<SendResponse> sendResponseList, List<MessageWithFcmToken> targetList
    ) {
        List<MessageWithFcmToken> failedMessageList = new ArrayList<>();
        List<String> deleteTokenList = new ArrayList<>();

        for (int i = 0; i < sendResponseList.size(); i++) {
            if (!sendResponseList.get(i).isSuccessful()) {
                MessagingErrorCode errorCode = sendResponseList.get(i).getException()
                    .getMessagingErrorCode();
                if (RetryableErrorCode.RETRYABLE.contains(errorCode)) {
                    failedMessageList.add(targetList.get(i));
                } else if (errorCode == MessagingErrorCode.UNREGISTERED) {
                    deleteTokenList.add(targetList.get(i).getFcmToken());
                }
            }
        }

        deleteTokens(deleteTokenList);

        return failedMessageList;
    }

    private void deleteTokens(List<String> deleteTokenList) {
        if (!deleteTokenList.isEmpty()) {
            CompletableFuture.runAsync(() -> {
                try {
                    fcmTokenMongoRepository.deleteAllById(deleteTokenList);
                    log.info("[{}] 삭제된 FCM 토큰 개수: {}", Thread.currentThread().getName(), deleteTokenList.size());
                } catch (Exception e) {
                    log.error("[{}] FCM 토큰 삭제 중 오류 발생: {}", Thread.currentThread().getName(), e.getMessage(), e);
                }
            });
        }
    }

}
