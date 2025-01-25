package api.domain.notice.controller.model.latestnotice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LatestThreeNoticeDto {

    private Long nttId;
    private String title;
    private String contentUrl;
    private String departmentName;
    private String registeredAt;

}
