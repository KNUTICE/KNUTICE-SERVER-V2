package api.domain.urgent.controller;

import api.domain.urgent.business.UrgentNoticeBusiness;
import api.domain.urgent.controller.model.UrgentNoticeResponse;
import global.api.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/open-api/urgent", "/open-api/urgent-notices"})
public class UrgentNoticeOpenApiController {

    private final UrgentNoticeBusiness urgentNoticeBusiness;

    @GetMapping({"", "/latest"})
    public Api<UrgentNoticeResponse> getUrgentNotice() {
        UrgentNoticeResponse urgentNoticeResponse = urgentNoticeBusiness.getUrgentNotice();
        return Api.OK(urgentNoticeResponse);
    }

}
