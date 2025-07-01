package api.domain.report.controller;

import api.domain.report.controller.model.ReportDetailResponse;
import api.domain.report.controller.model.ReportSimpleResponse;
import api.domain.report.business.ReportBusiness;
import global.api.Api;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportApiController {

    private final ReportBusiness reportBusiness;

    @GetMapping()
    public Api<List<ReportSimpleResponse>> getReportList() {
        List<ReportSimpleResponse> responseList = reportBusiness.getReportList();
        return Api.OK(responseList);
    }

    @GetMapping("/{reportId}")
    public Api<ReportDetailResponse> getReport(@PathVariable String reportId) {
        ReportDetailResponse response = reportBusiness.getReportBy(reportId);
        return Api.OK(response);
    }

}
