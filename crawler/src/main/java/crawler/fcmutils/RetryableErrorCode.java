package crawler.fcmutils;

import com.google.firebase.messaging.MessagingErrorCode;
import java.util.Set;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RetryableErrorCode {
    RETRYABLE(
        Set.of(
            MessagingErrorCode.INTERNAL,
            MessagingErrorCode.UNAVAILABLE,
            MessagingErrorCode.QUOTA_EXCEEDED
        )
    ),

    NOT_RETRYABLE(
        Set.of(
            MessagingErrorCode.THIRD_PARTY_AUTH_ERROR,
            MessagingErrorCode.INVALID_ARGUMENT,
            MessagingErrorCode.SENDER_ID_MISMATCH
        )
    );

    private final Set<MessagingErrorCode> errorCodes;

    public boolean contains(MessagingErrorCode errorCode) {
        return errorCodes.contains(errorCode);
    }

}