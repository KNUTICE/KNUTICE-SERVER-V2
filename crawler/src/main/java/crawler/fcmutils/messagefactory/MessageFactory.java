package crawler.fcmutils.messagefactory;

import com.google.firebase.messaging.Message;
import crawler.fcmutils.dto.FcmTokenDetail;
import crawler.service.model.FcmDto;
import org.springframework.stereotype.Component;

@Component
public class MessageFactory {

    public Message createMessage(FcmDto fcmDto, FcmTokenDetail fcmTokenDetail) {
        return Message.builder()
            .setToken(fcmTokenDetail.getFcmToken())
            .putAllData(DataMapFactory.createDataMap(fcmDto))
            .setNotification(NotificationFactory.createNotification(fcmDto))
            .setAndroidConfig(AndroidConfigFactory.createAndroidConfig(fcmDto))
            .setApnsConfig(ApnsConfigFactory.createApnsConfig(fcmDto, fcmTokenDetail))
            .build();

    }

}
