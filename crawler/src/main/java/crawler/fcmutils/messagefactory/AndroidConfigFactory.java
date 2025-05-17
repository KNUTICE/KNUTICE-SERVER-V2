package crawler.fcmutils.messagefactory;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.AndroidNotification.Priority;
import crawler.service.model.FcmDto;

public class AndroidConfigFactory {

    public static AndroidConfig createAndroidConfig(FcmDto fcmDto) {
        return AndroidConfig.builder()
            .setNotification(
                AndroidNotification.builder()
                    .setSound("default")
                    .setPriority(Priority.HIGH)
                    .build()
            )
            .build();
    }

}
