package api.domain.notice.controller.model.response;

import global.utils.NoticeMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponse {

    private Long nttId;

    private String title;

    private String contentUrl;

    private String contentImage;

    private String departmentName;

    private String registeredAt;

    private NoticeMapper noticeName;

}
