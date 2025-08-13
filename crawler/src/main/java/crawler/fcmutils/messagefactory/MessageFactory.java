package crawler.fcmutils.messagefactory;

import com.google.firebase.messaging.Message;
import crawler.service.model.FcmDto;
import org.springframework.stereotype.Component;

@Component
public class MessageFactory {

    public Message createMessage(FcmDto fcmDto, String token, boolean silentPush) {
        Message.Builder messageBuilder = Message.builder().setToken(token);

        if (silentPush) {
            messageBuilder.setApnsConfig(ApnsSilentPushFactory.createApnsConfig());
        } else {
            messageBuilder
                .putAllData(DataMapFactory.createDataMap(fcmDto))
                .setNotification(NotificationFactory.createNotification(fcmDto))
                .setAndroidConfig(AndroidConfigFactory.createAndroidConfig(fcmDto))
                .setApnsConfig(ApnsConfigFactory.createApnsConfig(fcmDto))
                .build();
        }

        return messageBuilder.build();
    }

}
