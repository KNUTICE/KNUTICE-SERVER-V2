package api.domain.notice.controller;

import api.domain.notice.business.NoticeBusiness;
import api.domain.notice.controller.model.latestnotice.LatestThreeNoticeResponse;
import api.domain.notice.controller.model.noticelist.NoticeRequest;
import api.domain.notice.controller.model.noticelist.NoticeResponse;
import global.api.Api;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api/notice")
@Slf4j
public class NoticeOpenApiController {

    private final NoticeBusiness noticeBusiness;

    @GetMapping("/list")
    public Api<List<NoticeResponse>> getNoticeList(
        @ModelAttribute @Valid NoticeRequest noticeRequest,
        @PageableDefault(sort = "nttId", direction = Sort.Direction.DESC, size = 20) Pageable page
    ) {
        log.info("api test : {}", noticeRequest);
        List<NoticeResponse> response = noticeBusiness.getNoticeList(noticeRequest, page);
        return Api.OK(response);
    }

    @GetMapping()
    public Api<LatestThreeNoticeResponse> getLatestThreeNotice() {
        LatestThreeNoticeResponse response = noticeBusiness.getLatestThreeNotice();
        return Api.OK(response);
    }

}
