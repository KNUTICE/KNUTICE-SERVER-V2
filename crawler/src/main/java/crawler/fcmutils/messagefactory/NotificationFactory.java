package crawler.fcmutils.messagefactory;

import com.google.firebase.messaging.Notification;
import crawler.service.model.FcmDto;

public class NotificationFactory {

    public static Notification createNotification(FcmDto fcmDto) {
        return Notification.builder()
            .setTitle(fcmDto.getTitle()) // 일반공지, 학사공지 등
            .setBody(fcmDto.getContent())
            .setImage(fcmDto.getContentImage())
            .build();
    }

}
