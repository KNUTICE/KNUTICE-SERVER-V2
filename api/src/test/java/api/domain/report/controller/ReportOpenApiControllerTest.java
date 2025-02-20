package api.domain.report.controller;

import static org.junit.jupiter.api.Assertions.*;

import api.common.exception.fcm.FcmTokenNotFoundException;
import api.config.AcceptanceTestWithMongo;
import api.domain.fcm.business.FcmTokenBusiness;
import api.domain.fcm.controller.model.FcmTokenRequest;
import api.domain.report.business.ReportBusiness;
import api.domain.report.controller.model.ReportRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReportOpenApiControllerTest extends AcceptanceTestWithMongo {

    @Autowired
    private ReportBusiness reportBusiness;

    @Autowired
    private FcmTokenBusiness fcmTokenBusiness;

    @Test
    void 레포트_제출_성공() {
        // Given
        FcmTokenRequest fcmTokenRequest = new FcmTokenRequest();
        fcmTokenRequest.setFcmToken("my_test_token");
        fcmTokenBusiness.saveFcmToken(fcmTokenRequest);

        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setFcmToken("my_test_token");

        // When
        Boolean isRegistered = reportBusiness.submitReport(reportRequest);

        // Then
        assertTrue(isRegistered);

    }

    @Test
    void 레포트_제출_실패_FCM토큰_없음() {
        // Given
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setFcmToken("my_test_token"); // 존재하지 않는 FCM 토큰

        // When & Then
        assertThrows(FcmTokenNotFoundException.class, () -> reportBusiness.submitReport(reportRequest));
    }

}