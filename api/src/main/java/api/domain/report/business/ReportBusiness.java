package api.domain.report.business;

import api.domain.report.controller.model.ReportRequest;
import api.domain.report.converter.ReportConverter;
import api.domain.report.service.ReportService;
import db.domain.report.ReportDocument;
import global.annotation.Business;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class ReportBusiness {

    private final ReportService reportService;
    private final ReportConverter reportConverter;

    public Boolean submitReport(ReportRequest reportRequest) {
        ReportDocument reportDocument = reportConverter.toDocuemnt(reportRequest);
        return reportService.submitReport(reportDocument);

    }
}
