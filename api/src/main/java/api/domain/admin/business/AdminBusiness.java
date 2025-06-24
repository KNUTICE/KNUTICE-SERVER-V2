package api.domain.admin.business;

import api.common.error.NoticeErrorCode;
import api.common.exception.notice.NoticeExistsException;
import api.common.exception.notice.NoticeNotFoundException;
import api.domain.admin.controller.model.request.NoticeSaveRequest;
import api.domain.admin.controller.model.request.NoticeUpdateRequest;
import api.domain.admin.controller.model.response.ReportDetailResponse;
import api.domain.admin.controller.model.response.ReportListResponse;
import api.domain.admin.controller.model.request.UrgentNoticeSaveRequest;
import api.domain.admin.controller.model.response.FcmTokenInfoList;
import api.domain.fcm.converter.FcmTokenConverter;
import api.domain.fcm.service.FcmTokenService;
import api.domain.notice.converter.NoticeConverter;
import api.domain.notice.service.NoticeService;
import api.domain.report.converter.ReportConverter;
import api.domain.report.service.ReportService;
import api.domain.urgent.converter.UrgentNoticeConverter;
import api.domain.urgent.service.UrgentNoticeService;
import db.domain.notice.NoticeDocument;
import db.domain.report.ReportDocument;
import db.domain.token.fcm.FcmTokenDocument;
import db.domain.urgent.UrgentNoticeDocument;
import global.annotation.Business;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class AdminBusiness {

    private final NoticeService noticeService;
    private final UrgentNoticeService urgentNoticeService;
    private final ReportService reportService;
    private final FcmTokenService fcmTokenService;

    private final NoticeConverter noticeConverter;
    private final UrgentNoticeConverter urgentNoticeConverter;
    private final ReportConverter reportConverter;
    private final FcmTokenConverter fcmTokenConverter;

    public Boolean saveNotice(NoticeSaveRequest noticeSaveRequest) {
        if (noticeService.existsNoticeBy(noticeSaveRequest.getNttId())) {
            throw new NoticeExistsException(NoticeErrorCode.NOTICE_ALREADY_EXISTS);
        }

        NoticeDocument noticeDocument = noticeConverter.toDocument(noticeSaveRequest);
        return noticeService.saveNoticeBy(noticeDocument);
    }

    public Boolean deleteNotice(Long nttId) {
        if (!noticeService.existsNoticeBy(nttId)) {
            throw new NoticeNotFoundException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }
        noticeService.deleteNotice(nttId);
        return true;
    }

    public Boolean updateNotice(NoticeUpdateRequest noticeUpdateRequest) {
        NoticeDocument noticeDocument = noticeService.getNoticeBy(noticeUpdateRequest.getNttId());
        noticeConverter.updateDocument(noticeDocument, noticeUpdateRequest);
        return noticeService.saveNoticeBy(noticeDocument);
    }

    public Boolean saveUrgentNotice(UrgentNoticeSaveRequest urgentNoticeSaveRequest) {
        // 기존 긴급 공지 모두 삭제
        urgentNoticeService.deleteAllUrgentNotice();

        UrgentNoticeDocument urgentNoticeDocument = urgentNoticeConverter.toDocument(
            urgentNoticeSaveRequest);
        return urgentNoticeService.saveUrgentNoticeBy(urgentNoticeDocument);
    }

    public Boolean deleteUrgentNotice() {
        return urgentNoticeService.deleteAllUrgentNotice();
    }

    public List<ReportListResponse> getReportList() {
        List<ReportDocument> reportDocumentList = reportService.getReportList();
        return reportConverter.toListResponse(reportDocumentList);
    }

    public ReportDetailResponse getReportBy(String reportId) {
        ReportDocument reportDocument = reportService.getReportBy(reportId);
        return reportConverter.toDetailResponse(reportDocument);
    }

    public List<FcmTokenInfoList> getFcmTokenList() {
        List<FcmTokenDocument> fcmTokenDocumentList = fcmTokenService.getFcmTokenList();
        return fcmTokenConverter.toListResponse(fcmTokenDocumentList);
    }

}
