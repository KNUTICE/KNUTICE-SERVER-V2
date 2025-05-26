package db.domain.token.fcm;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fcm_token")
public class FcmTokenDocument {

    @Id
    private String fcmToken;

    @Builder.Default
    private boolean generalNewsTopic = true;

    @Builder.Default
    private boolean scholarshipNewsTopic = true;

    @Builder.Default
    private boolean eventNewsTopic = true;

    @Builder.Default
    private boolean academicNewsTopic = true;

    @Builder.Default
    private boolean employmentNewsTopic = false;

    private LocalDateTime registeredAt;

    @Builder.Default
    private int failedCount = 0;

}
