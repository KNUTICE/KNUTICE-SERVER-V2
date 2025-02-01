package api.domain.admin.controller;

import api.domain.admin.business.AdminBusiness;
import api.domain.admin.controller.model.request.NoticeSaveRequest;
import api.domain.admin.controller.model.request.NoticeUpdateRequest;
import api.domain.admin.controller.model.response.ReportDetailResponse;
import api.domain.admin.controller.model.response.ReportListResponse;
import api.domain.admin.controller.model.request.UrgentNoticeSaveRequest;
import api.domain.admin.controller.model.response.FcmTokenInfoList;
import global.api.Api;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminBusiness adminBusiness;

    @PostMapping()
    public Api<Boolean> saveNotice(
        @RequestBody @Valid Api<NoticeSaveRequest> noticeSaveRequestApi
    ) {
        Boolean response = adminBusiness.saveNotice(noticeSaveRequestApi.getBody());
        return Api.OK(response);
    }

    @PostMapping("/{nttId}")
    public Api<Boolean> deleteNotice(@PathVariable Long nttId) {
        Boolean response = adminBusiness.deleteNotice(nttId);
        return Api.OK(response);
    }

    @PostMapping("/update")
    public Api<Boolean> updateNotice(
        @RequestBody @Valid Api<NoticeUpdateRequest> noticeUpdateRequest
    ) {
        Boolean response = adminBusiness.updateNotice(noticeUpdateRequest.getBody());
        return Api.OK(response);
    }

    @PostMapping("/urgent")
    public Api<Boolean> saveUrgentNotice(
        @RequestBody @Valid Api<UrgentNoticeSaveRequest> urgentNoticeSaveRequest
    ) {
        Boolean response = adminBusiness.saveUrgentNotice(urgentNoticeSaveRequest.getBody());
        return Api.OK(response);
    }

    @PostMapping("/urgent/delete")
    public Api<Boolean> deleteAllUrgentNotice() {
        Boolean response = adminBusiness.deleteAllUrgentNotice();
        return Api.OK(response);
    }

    @GetMapping("/report")
    public Api<List<ReportListResponse>> getReportList() {
        List<ReportListResponse> responseList = adminBusiness.getReportList();
        return Api.OK(responseList);
    }

    @GetMapping("/report/{reportId}")
    public Api<ReportDetailResponse> getReport(@PathVariable String reportId) {
        ReportDetailResponse response = adminBusiness.getReportBy(reportId);
        return Api.OK(response);
    }

    @GetMapping("/fcm-tokens")
    public Api<List<FcmTokenInfoList>> getFcmTokenList() {
        List<FcmTokenInfoList> responseList = adminBusiness.getFcmTokenList();
        return Api.OK(responseList);
    }

}
