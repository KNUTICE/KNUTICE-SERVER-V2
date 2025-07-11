package api.domain.notice.controller.model.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeSearchRequest {

    @Size(min = 1, max = 50)
    private String keyword;

    private Long nttId;

}
