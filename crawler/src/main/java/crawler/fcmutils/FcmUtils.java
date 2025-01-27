package crawler.fcmutils;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import crawler.service.model.FcmDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FcmUtils {

    public Message createMessageBuilder (FcmDto dto, String token) {
        return Message.builder()
            .setToken(token)
//            .setTopic(topic)
            .setNotification(
                Notification.builder()
                    .setTitle(dto.getTitle())
                    .setBody(dto.getContent())
//                        .setImage(dto.getContentImage())
                    .build()
            )
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setNotification(
                        AndroidNotification.builder()
                            .setSound("default")
                            .build()
                    )
                    .build()
            )
            .setApnsConfig(
                ApnsConfig.builder()
                    .setAps(Aps.builder()
                        .setAlert(ApsAlert.builder()
//                                .setLaunchImage(dto.getContentImage())
                            .build())
                        .setSound("default")
                        .build())
                    .build()
            )
            .build();
    }

    public List<Message> createMessageBuilderList(FcmDto dto, List<String> tokenList) {
        return tokenList.stream()
            .map(token -> createMessageBuilder(dto, token))
            .toList();
    }

}