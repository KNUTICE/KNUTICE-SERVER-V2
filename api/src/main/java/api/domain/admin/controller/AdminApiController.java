package api.domain.admin.controller;

import api.common.feign.CrawlerClient;
import api.common.feign.request.FcmRequest;
import api.domain.admin.business.AdminBusiness;
import api.domain.admin.controller.model.request.NoticeSaveRequest;
import api.domain.admin.controller.model.request.NoticeUpdateRequest;
import api.domain.admin.controller.model.response.ReportDetailResponse;
import api.domain.admin.controller.model.response.ReportListResponse;
import api.domain.admin.controller.model.request.UrgentNoticeSaveRequest;
import api.domain.admin.controller.model.response.FcmTokenInfoList;
import api.domain.image.business.ImageBusiness;
import db.domain.image.enums.ImageKind;
import global.api.Api;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminBusiness adminBusiness;
    private final ImageBusiness imageBusiness;

    private final CrawlerClient crawlerClient;

    @PostMapping("/notices")
    public Api<Boolean> saveNotice(@RequestBody @Valid Api<NoticeSaveRequest> noticeSaveRequest) {
        Boolean response = adminBusiness.saveNotice(noticeSaveRequest.getBody());
        return Api.OK(response);
    }

    @DeleteMapping("/notices/{nttId}")
    public Api<Boolean> deleteNotice(@PathVariable Long nttId) {
        Boolean response = adminBusiness.deleteNotice(nttId);
        return Api.OK(response);
    }

    @PutMapping("/notices")
    public Api<Boolean> updateNotice(
        @RequestBody @Valid Api<NoticeUpdateRequest> noticeUpdateRequest
    ) {
        Boolean response = adminBusiness.updateNotice(noticeUpdateRequest.getBody());
        return Api.OK(response);
    }

    @PostMapping("/urgent-notices")
    public Api<Boolean> saveUrgentNotice(
        @RequestBody @Valid Api<UrgentNoticeSaveRequest> urgentNoticeSaveRequest
    ) {
        Boolean response = adminBusiness.saveUrgentNotice(urgentNoticeSaveRequest.getBody());
        return Api.OK(response);
    }

    @DeleteMapping("/urgent-notices")
    public Api<Boolean> deleteAllUrgentNotice() {
        Boolean response = adminBusiness.deleteUrgentNotice();
        return Api.OK(response);
    }

    @GetMapping("/reports")
    public Api<List<ReportListResponse>> getReportList() {
        List<ReportListResponse> responseList = adminBusiness.getReportList();
        return Api.OK(responseList);
    }

    @GetMapping("/reports/{reportId}")
    public Api<ReportDetailResponse> getReport(@PathVariable String reportId) {
        ReportDetailResponse response = adminBusiness.getReportBy(reportId);
        return Api.OK(response);
    }

    @GetMapping("/fcm-tokens")
    public Api<List<FcmTokenInfoList>> getFcmTokenList() {
        List<FcmTokenInfoList> responseList = adminBusiness.getFcmTokenList();
        return Api.OK(responseList);
    }

    @PostMapping("/messages")
    public Api<Boolean> sendMessage(@RequestBody @Valid Api<FcmRequest> request) {
        crawlerClient.sendMessage(request.getBody().getFcmToken(), request.getBody());
        return Api.OK(true);
    }

    @PostMapping("/images")
    public Api<Boolean> uploadImage(
        @RequestPart("image") MultipartFile multipartFile,
        @RequestParam ImageKind imageKind
    ) {
        imageBusiness.uploadImage(multipartFile, imageKind);
        return Api.OK(true);
    }

    @DeleteMapping("/images")
    public Api<Boolean> deleteImage(@RequestParam String imageId) {
        imageBusiness.deleteImage(imageId);
        return Api.OK(true);
    }

}
