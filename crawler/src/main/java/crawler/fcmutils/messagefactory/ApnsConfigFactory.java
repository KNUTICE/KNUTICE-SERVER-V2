package crawler.fcmutils.messagefactory;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import crawler.fcmutils.dto.FcmTokenDetail;
import crawler.service.model.FcmDto;

public class ApnsConfigFactory {

    public static ApnsConfig createApnsConfig(FcmDto fcmDto, FcmTokenDetail fcmTokenDetail) {

        Aps.Builder apsBuilder = Aps.builder()
            .setAlert(ApsAlert.builder()
                .setLaunchImage(fcmDto.getContentImage())
                .build())
            .setSound("default");

        // apnsEnabled 가 true 이면 badge 설정
        if (Boolean.TRUE.equals(fcmTokenDetail.getApnsEnabled())) { // Null safe
            apsBuilder.setBadge(fcmTokenDetail.getBadgeCount());
        }

        return ApnsConfig.builder()
            .setAps(apsBuilder.build())
            .build();
    }

}
