package api.domain.report.business;

import api.common.error.FcmTokenErrorCode;
import api.common.exception.fcm.FcmTokenNotFoundException;
import api.domain.fcm.service.FcmTokenSeconService;
import api.domain.report.controller.model.ReportDetailResponse;
import api.domain.report.controller.model.ReportSimpleResponse;
import api.domain.fcm.service.FcmTokenService;
import api.domain.report.controller.model.ReportRequest;
import api.domain.report.converter.ReportConverter;
import api.domain.report.service.ReportService;
import api.domain.report.service.SlackService;
import db.domain.report.ReportDocument;
import global.annotation.Business;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class ReportBusiness {

    private final ReportService reportService;
    private final FcmTokenService fcmTokenService;
    private final FcmTokenSeconService fcmTokenSeconService;
    private final SlackService slackService;

    private final ReportConverter reportConverter;

    // 토큰 조회만 SecondaryDB 에서 조회, Report 저장은 PrimaryDB
    public Boolean submitReport(ReportRequest reportRequest) {
        boolean existsFcmToken = fcmTokenSeconService.existsBy(reportRequest.getFcmToken());
        if (!existsFcmToken) {
            throw new FcmTokenNotFoundException(FcmTokenErrorCode.TOKEN_NOT_FOUND);
        }

        ReportDocument reportDocument = reportConverter.toDocument(reportRequest);
        slackService.sendReportNotificationAsync(reportDocument);
        return reportService.submitReport(reportDocument);
    }

    /**
     * 관리자가 접수된 모든 신고 목록을 조회하는 메서드.
     *
     * @return 신고 목록 응답 리스트
     */
    public List<ReportSimpleResponse> getReportList() {
        List<ReportDocument> reportDocumentList = reportService.getReportList();
        return reportConverter.toListResponse(reportDocumentList);
    }

    /**
     * 관리자가 특정 신고의 상세 내용을 조회하는 메서드.
     *
     * @param reportId 조회할 신고의 ID
     * @return 신고 상세 응답 객체
     */
    public ReportDetailResponse getReportBy(String reportId) {
        ReportDocument reportDocument = reportService.getReportBy(reportId);
        return reportConverter.toDetailResponse(reportDocument);
    }



}
