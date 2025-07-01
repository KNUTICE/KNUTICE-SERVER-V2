package api.domain.fcm.controller.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FcmTokenInfo {

    private String fcmToken;

    private boolean generalNewsTopic;

    private boolean scholarshipNewsTopic;

    private boolean eventNewsTopic;

    private boolean academicNewsTopic;

    private boolean employmentNewsTopic;

    private LocalDateTime registeredAt;

    private int failedCount;

}
