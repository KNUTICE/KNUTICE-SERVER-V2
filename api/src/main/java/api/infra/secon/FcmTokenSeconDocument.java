package api.infra.secon;

import global.utils.DeviceType;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "fcm_token")
@SuperBuilder
@NoArgsConstructor
public class FcmTokenSeconDocument extends MongoBaseDocument {


    @Id
    private String fcmToken;

    @Builder.Default
    private Set<String> subscribedNoticeTopics = new HashSet<>();

    @Builder.Default
    private Set<String> subscribedMajorTopics = new HashSet<>();

    @Builder.Default
    private Set<String> subscribedMealTopics = new HashSet<>();

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private DeviceType deviceType = DeviceType.AOS;

}
