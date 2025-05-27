package api.domain.fcm.controller;

import static org.junit.jupiter.api.Assertions.*;

import api.config.AcceptanceTestWithMongo;
import api.domain.fcm.business.FcmTokenBusiness;
import api.domain.fcm.controller.model.FcmTokenRequest;
import api.domain.fcm.service.FcmTokenService;
import db.domain.token.fcm.FcmTokenDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FcmTokenOpenApiControllerTest extends AcceptanceTestWithMongo {

    @Autowired
    private FcmTokenBusiness fcmTokenBusiness;

    @Autowired
    private FcmTokenService fcmTokenService;

    @Test
    void 토큰_저장_성공() {

        // Given
        FcmTokenRequest fcmTokenRequest = new FcmTokenRequest();
        fcmTokenRequest.setFcmToken("my_test_token");

        // When
        Boolean isRegistered = fcmTokenBusiness.saveFcmToken(fcmTokenRequest);

        // Then
        FcmTokenDocument fcmTokenDocument = fcmTokenService.getFcmTokenBy(fcmTokenRequest.getFcmToken());

        assertEquals(fcmTokenRequest.getFcmToken(), fcmTokenDocument.getFcmToken());
        assertTrue(isRegistered);

    }

}
