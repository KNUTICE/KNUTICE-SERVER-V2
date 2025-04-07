package api.domain.notice.controller.model.latestnotice;

import api.domain.notice.controller.model.noticelist.NoticeResponse;
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

    private List<NoticeResponse> latestThreeGeneralNews;
    private List<NoticeResponse> latestThreeScholarshipNews;
    private List<NoticeResponse> latestThreeEventNews;
    private List<NoticeResponse> latestThreeAcademicNews;
    private List<NoticeResponse> latestEmploymentNews;

}
