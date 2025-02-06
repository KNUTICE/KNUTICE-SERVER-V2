package api.domain.report.controller;

import api.domain.report.business.ReportBusiness;
import api.domain.report.controller.model.ReportRequest;
import global.api.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api/report")
public class ReportOpenApiController {

    private final ReportBusiness reportBusiness;

    @PostMapping()
    public Api<Boolean> submitReport(@RequestBody @Valid Api<ReportRequest> reportRequest) {
        Boolean response = reportBusiness.submitReport(reportRequest.getBody());
        return Api.OK(response);
    }

}
