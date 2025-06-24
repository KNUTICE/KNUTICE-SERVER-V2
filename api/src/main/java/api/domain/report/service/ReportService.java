package api.domain.report.service;

import api.common.error.ReportErrorCode;
import api.common.exception.report.ReportNotFoundException;
import db.domain.report.ReportDocument;
import db.domain.report.ReportMongoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportMongoRepository reportMongoRepository;

    public boolean submitReport(ReportDocument reportDocument) {
        ReportDocument savedReportDocument = reportMongoRepository.save(reportDocument);
        return savedReportDocument.getId() != null;
    }

    public List<ReportDocument> getReportList() {
        return reportMongoRepository.findAll();
    }

    public ReportDocument getReportBy(String reportId) {
        return reportMongoRepository.findById(reportId)
            .orElseThrow(() -> new ReportNotFoundException(ReportErrorCode.REPORT_NOT_FOUND));
    }

}
