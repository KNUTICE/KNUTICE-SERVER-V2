package crawler.fcmutils;

import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
import crawler.fcmutils.dto.FilteredFcmResult;
import crawler.fcmutils.dto.MessageWithFcmToken;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmMessageFilter {

    public FilteredFcmResult filterFailedMessage(
        List<SendResponse> sendResponseList, List<MessageWithFcmToken> targetList
    ) {
        List<MessageWithFcmToken> failedMessageList = new ArrayList<>();
        List<String> deleteTokenList = new ArrayList<>();

        for (int i = 0; i < sendResponseList.size(); i++) {
            if (!sendResponseList.get(i).isSuccessful()) {
                MessagingErrorCode errorCode = sendResponseList.get(i).getException()
                    .getMessagingErrorCode();
                if (RetryableErrorCode.RETRYABLE.contains(errorCode) || errorCode == null) {
                    failedMessageList.add(targetList.get(i));
                } else if (errorCode == MessagingErrorCode.UNREGISTERED) {
                    deleteTokenList.add(targetList.get(i).getFcmToken());
                }
            }
        }

        log.info("[ALERT] UNREGISTER TOKEN 개수 : {}", deleteTokenList.size());
        return new FilteredFcmResult(failedMessageList, deleteTokenList);

    }
}
