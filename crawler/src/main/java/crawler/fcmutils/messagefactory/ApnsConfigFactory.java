package crawler.fcmutils.messagefactory;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import crawler.service.model.FcmDto;

public class ApnsConfigFactory {

    public static ApnsConfig createApnsConfig(FcmDto fcmDto) {
        return ApnsConfig.builder()
            .putHeader("apns-priority", "10")
            .setAps(Aps.builder()
                .setMutableContent(true)
                .setAlert(ApsAlert.builder()
                    .setLaunchImage(fcmDto.getContentImage())
                    .build())
                .setSound("default")
                .build()
            ).build();
    }

}
