package api.domain.notice.controller.model.sync;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeSyncRequest {

    @NotEmpty
    private List<Long> nttIdList;

}
