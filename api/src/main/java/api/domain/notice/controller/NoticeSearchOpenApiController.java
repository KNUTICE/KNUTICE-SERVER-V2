package api.domain.notice.controller;

import api.domain.notice.business.NoticeSearchBusiness;
import api.domain.notice.controller.model.response.NoticeResponse;
import api.domain.notice.controller.model.request.NoticeSearchRequest;
import global.api.Api;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api/search")
public class NoticeSearchOpenApiController {

    private final NoticeSearchBusiness noticeSearchBusiness;

    @GetMapping()
    public Api<List<NoticeResponse>> getNoticeList(
        @ModelAttribute @Valid NoticeSearchRequest NoticeSearchRequest,
        @PageableDefault(sort = "nttId", direction = Sort.Direction.DESC, size = 20) Pageable page)
    {
        List<NoticeResponse> response = noticeSearchBusiness.getSearchBy(NoticeSearchRequest, page);
        return Api.OK(response);
    }


}
