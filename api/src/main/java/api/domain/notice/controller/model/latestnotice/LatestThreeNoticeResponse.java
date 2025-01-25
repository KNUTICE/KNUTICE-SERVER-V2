package api.domain.notice.controller.model.latestnotice;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LatestThreeNoticeResponse {

    private List<LatestThreeNoticeDto> latestThreeGeneralNews;
    private List<LatestThreeNoticeDto> latestThreeScholarshipNews;
    private List<LatestThreeNoticeDto> latestThreeEventNews;
    private List<LatestThreeNoticeDto> latestThreeAcademicNews;

}
