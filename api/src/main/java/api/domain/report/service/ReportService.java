package api.domain.report.service;

import db.domain.report.ReportDocument;
import db.domain.report.ReportMongoRepository;
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

}
