package api.domain.notice.controller.model.search;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeSearchRequest {

    @Size(min = 2, max = 50)
    @NotBlank
    private String keyword;

    private Long nttId;

}
