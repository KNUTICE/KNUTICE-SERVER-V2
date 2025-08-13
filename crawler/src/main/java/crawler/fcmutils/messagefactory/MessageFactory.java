package crawler.fcmutils.messagefactory;

import com.google.firebase.messaging.Message;
import crawler.service.model.FcmDto;
import org.springframework.stereotype.Component;

@Component
public class MessageFactory {

    public Message createMessage(FcmDto fcmDto, String token, boolean silentPush) {
        Message.Builder messageBuilder = Message.builder().setToken(token);

        if (silentPush) {
            messageBuilder
                .setApnsConfig(ApnsSilentPushFactory.createApnsConfig())
                .setNotification(NotificationFactory.createNotification(fcmDto));
        } else {
            messageBuilder
                .putAllData(DataMapFactory.createDataMap(fcmDto))
                .setNotification(NotificationFactory.createNotification(fcmDto))
                .setAndroidConfig(AndroidConfigFactory.createAndroidConfig(fcmDto))
                .setApnsConfig(ApnsConfigFactory.createApnsConfig(fcmDto));
        }

        return messageBuilder.build();
    }

}
