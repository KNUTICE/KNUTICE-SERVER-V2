package api.domain.notice.controller;

import api.domain.notice.business.NoticeBusiness;
import api.domain.notice.controller.model.response.LatestThreeNoticeResponse;
import api.domain.notice.controller.model.request.NoticeRequest;
import api.domain.notice.controller.model.response.NoticeResponse;
import api.domain.notice.controller.model.request.NoticeSyncRequest;
import global.api.Api;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeOpenApiController {

    private final NoticeBusiness noticeBusiness;

    @GetMapping({"/open-api/notice/list", "/open-api/notices"})
    public Api<List<NoticeResponse>> getNoticeList(
        @ModelAttribute @Valid NoticeRequest noticeRequest,
        @PageableDefault(sort = "nttId", direction = Sort.Direction.DESC, size = 20) Pageable page
    ) {
        List<NoticeResponse> response = noticeBusiness.getNoticeList(noticeRequest, page);
        return Api.OK(response);
    }

    @GetMapping({"/open-api/notice", "/open-api/notices/latest"})
    public Api<LatestThreeNoticeResponse> getLatestThreeNotice() {
        LatestThreeNoticeResponse response = noticeBusiness.getLatestThreeNotice();
        return Api.OK(response);
    }

    @GetMapping({"/open-api/notice/{nttId}", "/open-api/notices/{nttId}"})
    public Api<NoticeResponse> getNotice(@PathVariable Long nttId) {
        NoticeResponse response = noticeBusiness.getNoticeBy(nttId);
        return Api.OK(response);
    }

    // 북마크 Null 공백을 방지하기 위한 API 입니다.
    @PostMapping({"/open-api/notice/sync", "/open-api/notices/sync"})
    public Api<List<NoticeResponse>> getNotice(
        @RequestBody @Valid Api<NoticeSyncRequest> noticeSyncRequest
    ) {
        List<NoticeResponse> response = noticeBusiness.getNoticeList(noticeSyncRequest.getBody());
        return Api.OK(response);
    }

}
