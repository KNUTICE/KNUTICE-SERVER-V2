package api.domain.report.business;

import api.common.error.FcmTokenErrorCode;
import api.common.exception.fcm.FcmException;
import api.domain.fcm.service.FcmTokenService;
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
    private final FcmTokenService fcmTokenService;

    private final ReportConverter reportConverter;

    public Boolean submitReport(ReportRequest reportRequest) {

        boolean existsFcmToken = fcmTokenService.existsBy(reportRequest.getFcmToken());

        if (!existsFcmToken) {
            throw new FcmException.FcmTokenNotFoundException(FcmTokenErrorCode.TOKEN_NOT_FOUND);
        }

        ReportDocument reportDocument = reportConverter.toDocument(reportRequest);
        return reportService.submitReport(reportDocument);
    }

}
