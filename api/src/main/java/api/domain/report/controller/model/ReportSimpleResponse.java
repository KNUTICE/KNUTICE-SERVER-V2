package api.domain.report.controller.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportSimpleResponse {

    private String reportId;

    private String content;

    private LocalDateTime registeredAt;

}
