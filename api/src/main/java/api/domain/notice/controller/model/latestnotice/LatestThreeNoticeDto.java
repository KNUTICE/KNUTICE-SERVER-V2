package api.domain.notice.controller.model.latestnotice;

import global.utils.NoticeMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatestThreeNoticeDto {

    private Long nttId;
    private String title;
    private String contentUrl;
    private String departmentName;
    private String registeredAt;
    private NoticeMapper noticeName;

}
