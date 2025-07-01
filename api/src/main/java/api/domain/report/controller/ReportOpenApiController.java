package api.domain.report.controller;

import api.domain.report.business.ReportBusiness;
import api.domain.report.controller.model.ReportRequest;
import global.api.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/open-api/report", "/open-api/reports"})
public class ReportOpenApiController {

    private final ReportBusiness reportBusiness;

    @PostMapping()
    public Api<Boolean> submitReport(@RequestBody @Valid Api<ReportRequest> reportRequest) {
        Boolean response = reportBusiness.submitReport(reportRequest.getBody());
        log.info("[ALERT] REPORT 요청 : {}", reportRequest.getBody().getContent());
        return Api.OK(response);
    }

}
