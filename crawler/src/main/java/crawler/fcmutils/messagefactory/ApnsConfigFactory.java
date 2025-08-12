package crawler.fcmutils.messagefactory;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import crawler.service.model.FcmDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApnsConfigFactory {

    public static ApnsConfig createApnsConfig(FcmDto fcmDto, boolean silentPush) {

        ApnsConfig.Builder apnsBuilder = ApnsConfig.builder();

        if (silentPush) {
            apnsBuilder
                .putHeader("apns-priority", "5")
                .putHeader("apns-push-type", "background")
                .putCustomData("event", "token_update")
                .setAps(Aps.builder()
                    .setContentAvailable(true)
                    .setMutableContent(true)
                    .build());
        } else {
            apnsBuilder
                .putHeader("apns-priority", "10")
                .setAps(Aps.builder()
                    .setMutableContent(true)
                    .setAlert(ApsAlert.builder()
                        .setLaunchImage(fcmDto.getContentImage())
                        .build())
                    .setSound("default")
                    .build());
        }
        return apnsBuilder.build();
    }
}