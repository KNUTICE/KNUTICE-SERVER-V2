package api.domain.notice.controller.model.noticelist;

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
    private Integer contentNumber;
    private String title;
    private String contentUrl;
    private String contentImage;
    private String departName;
    private String registeredAt;

}
