package api.domain.notice.controller.model.noticelist;

import global.utils.NoticeMapper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequest {

    @NotNull
    private NoticeMapper noticeName;

    private Long nttId;

}
