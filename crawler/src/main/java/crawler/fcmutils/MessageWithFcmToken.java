package crawler.fcmutils;

import com.google.firebase.messaging.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageWithFcmToken {

    private final Message message;
    private final String fcmToken;

}

