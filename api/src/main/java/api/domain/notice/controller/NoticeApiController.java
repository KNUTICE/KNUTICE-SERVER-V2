package api.domain.notice.controller;

import api.domain.notice.controller.model.request.NoticeSaveRequest;
import api.domain.notice.controller.model.request.NoticeUpdateRequest;
import api.domain.notice.business.NoticeBusiness;
import global.api.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeApiController {

    private final NoticeBusiness noticeBusiness;

    @PostMapping()
    public Api<Boolean> saveNotice(@RequestBody @Valid Api<NoticeSaveRequest> noticeSaveRequest) {
        Boolean response = noticeBusiness.saveNotice(noticeSaveRequest.getBody());
        return Api.OK(response);
    }

    @DeleteMapping("/{nttId}")
    public Api<Boolean> deleteNotice(@PathVariable Long nttId) {
        Boolean response = noticeBusiness.deleteNotice(nttId);
        return Api.OK(response);
    }

    @PutMapping("/notices")
    public Api<Boolean> updateNotice(
        @RequestBody @Valid Api<NoticeUpdateRequest> noticeUpdateRequest
    ) {
        Boolean response = noticeBusiness.updateNotice(noticeUpdateRequest.getBody());
        return Api.OK(response);
    }

}
