package api.domain.admin.controller.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoList {

    private String fcmToken;

    private boolean generalNewsTopic;

    private boolean scholarshipNewsTopic;

    private boolean eventNewsTopic;

    private boolean academicNewsTopic;

    private LocalDateTime registeredAt;

    private int failedCount;

}
