package api.domain.admin.business;

import api.common.error.NoticeErrorCode;
import api.common.exception.notice.NoticeExistsException;
import api.common.exception.notice.NoticeNotFoundException;
import api.domain.admin.controller.model.request.NoticeSaveRequest;
import api.domain.admin.controller.model.request.NoticeUpdateRequest;
import api.domain.admin.controller.model.response.ReportDetailResponse;
import api.domain.admin.controller.model.response.ReportListResponse;
import api.domain.admin.controller.model.request.UrgentNoticeSaveRequest;
import api.domain.admin.controller.model.response.UserInfoList;
import api.domain.admin.service.AdminService;
import api.domain.fcm.converter.FcmTokenConverter;
import api.domain.notice.converter.NoticeConverter;
import api.domain.notice.service.NoticeService;
import api.domain.report.converter.ReportConverter;
import api.domain.urgent.converter.UrgentNoticeConverter;
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

    private final AdminService adminService;
    private final NoticeService noticeService;

    private final NoticeConverter noticeConverter;
    private final UrgentNoticeConverter urgentNoticeConverter;
    private final ReportConverter reportConverter;
    private final FcmTokenConverter fcmTokenConverter;

    public Boolean saveNotice(NoticeSaveRequest noticeSaveRequest) {

        Boolean existsNotice = noticeService.existsNoticeBy(noticeSaveRequest.getNttId());

        if (existsNotice) {
            throw new NoticeExistsException(NoticeErrorCode.NOTICE_ALREADY_EXISTS);
        }

        NoticeDocument noticeDocument = noticeConverter.toDocument(noticeSaveRequest);
        return adminService.saveNoticeBy(noticeDocument);
    }

    public Boolean deleteNotice(Long nttId) {

        Boolean existsNotice = noticeService.existsNoticeBy(nttId);

        if (!existsNotice) {
            throw new NoticeNotFoundException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }
        adminService.deleteNotice(nttId);
        return true;
    }

    public Boolean updateNotice(NoticeUpdateRequest noticeUpdateRequest) {
        NoticeDocument noticeDocument = noticeService.getNoticeBy(noticeUpdateRequest.getNttId());

        setNotice(noticeUpdateRequest, noticeDocument);

        return adminService.saveNoticeBy(noticeDocument);
    }

    private static void setNotice(
        NoticeUpdateRequest noticeUpdateRequest, NoticeDocument noticeDocument
    ) {
        noticeDocument.setContentNumber(noticeUpdateRequest.getContentNumber());
        noticeDocument.setTitle(noticeUpdateRequest.getTitle());
        noticeDocument.setContentUrl(noticeUpdateRequest.getContentUrl());
        noticeDocument.setContentImage(noticeUpdateRequest.getContentImage());
        noticeDocument.setDepartmentName(noticeUpdateRequest.getDepartmentName());
        noticeDocument.setRegisteredAt(noticeUpdateRequest.getRegisteredAt());
        noticeDocument.setNoticeName(noticeUpdateRequest.getNoticeName());
    }

    public Boolean saveUrgentNotice(UrgentNoticeSaveRequest urgentNoticeSaveRequest) {
        UrgentNoticeDocument urgentNoticeDocument = urgentNoticeConverter.toDocument(
            urgentNoticeSaveRequest);
        return adminService.saveUrgentNoticeBy(urgentNoticeDocument);
    }

    public Boolean deleteAllUrgentNotice() {
        return adminService.deleteAllUrgentNotice();
    }

    public List<ReportListResponse> getReportList() {
        List<ReportDocument> reportDocumentList = adminService.getReportList();
        return reportConverter.toListResponse(reportDocumentList);
    }

    public ReportDetailResponse getReportBy(String reportId) {
        ReportDocument reportDocument = adminService.getReportBy(reportId);
        return reportConverter.toDetailResponse(reportDocument);
    }

    public List<UserInfoList> getUserList() {
        List<FcmTokenDocument> fcmTokenDocumentList = adminService.getUserList();
        return fcmTokenConverter.toListResponse(fcmTokenDocumentList);
    }

}
