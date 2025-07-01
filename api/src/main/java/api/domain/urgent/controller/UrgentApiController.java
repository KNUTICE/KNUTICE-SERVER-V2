package api.domain.urgent.controller;

import api.domain.urgent.controller.model.UrgentNoticeSaveRequest;
import api.domain.urgent.business.UrgentNoticeBusiness;
import global.api.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/urgent-notices")
public class UrgentApiController {

    private final UrgentNoticeBusiness urgentNoticeBusiness;

    @PostMapping
    public Api<Boolean> saveUrgentNotice(
        @RequestBody @Valid Api<UrgentNoticeSaveRequest> urgentNoticeSaveRequest
    ) {
        Boolean response = urgentNoticeBusiness.saveUrgentNotice(urgentNoticeSaveRequest.getBody());
        return Api.OK(response);
    }

    @DeleteMapping
    public Api<Boolean> deleteAllUrgentNotice() {
        Boolean response = urgentNoticeBusiness.deleteUrgentNotice();
        return Api.OK(response);
    }


}
