package api.domain.admin.controller.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportListResponse {

    private String reportId;

    private String content;

    private LocalDateTime registeredAt;

}
