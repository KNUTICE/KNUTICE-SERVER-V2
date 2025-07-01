package api.domain.report.controller.model;

import db.domain.report.enums.ClientType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDetailResponse {

    private String reportId;

    private String fcmToken;

    private String content;

    private ClientType clientType;

    private String deviceName;

    private String version;

    private LocalDateTime registeredAt;

}
