package crawler.fcmutils.messagefactory;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;

public class ApnsSilentPushFactory {

    public static ApnsConfig createApnsConfig() {
        return ApnsConfig.builder()
            .putHeader("apns-priority", "5")
            .putHeader("apns-push-type", "background")
            .putCustomData("event", "token_update")
            .setAps(Aps.builder()
                .setContentAvailable(true)
                .setMutableContent(true)
                .build()
            )
            .build();
    }

}
