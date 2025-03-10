package api.domain.admin.service;

import api.common.error.ReportErrorCode;
import api.common.exception.report.ReportException;
import db.domain.notice.NoticeDocument;
import db.domain.notice.NoticeMongoRepository;
import db.domain.report.ReportDocument;
import db.domain.report.ReportMongoRepository;
import db.domain.token.fcm.FcmTokenDocument;
import db.domain.token.fcm.FcmTokenMongoRepository;
import db.domain.urgent.UrgentNoticeDocument;
import db.domain.urgent.UrgentNoticeMongoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final NoticeMongoRepository noticeMongoRepository;
    private final UrgentNoticeMongoRepository urgentNoticeMongoRepository;
    private final ReportMongoRepository reportMongoRepository;
    private final FcmTokenMongoRepository fcmTokenMongoRepository;

    public void deleteNotice(Long nttId) {
        noticeMongoRepository.deleteById(nttId);
    }

    public Boolean saveNoticeBy(NoticeDocument noticeDocument) {
        NoticeDocument savedNoticeDocument = noticeMongoRepository.save(noticeDocument);
        return savedNoticeDocument.getNttId() != null;
    }

    public Boolean saveUrgentNoticeBy(UrgentNoticeDocument urgentNoticeDocument) {
        UrgentNoticeDocument savedUrgentNotice = urgentNoticeMongoRepository.save(
            urgentNoticeDocument);
        return savedUrgentNotice.getId() != null;
    }

    public Boolean deleteAllUrgentNotice() {
        urgentNoticeMongoRepository.deleteAll();
        return true;
    }

    public List<ReportDocument> getReportList() {
        return reportMongoRepository.findAll();
    }

    public ReportDocument getReportBy(String reportId) {
        return reportMongoRepository.findById(reportId)
            .orElseThrow(() -> new ReportException.ReportNotFoundException(ReportErrorCode.REPORT_NOT_FOUND));
    }

    public List<FcmTokenDocument> getFcmTokenList() {
        return fcmTokenMongoRepository.findAll();
    }

}
