package api.domain.urgent.controller.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrgentNoticeResponse {

    private String title;

    private String content;

    private String contentUrl;

    private LocalDate registeredAt;

}
