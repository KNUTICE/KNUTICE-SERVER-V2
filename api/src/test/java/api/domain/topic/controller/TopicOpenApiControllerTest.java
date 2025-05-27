package api.domain.topic.controller;

import static org.junit.jupiter.api.Assertions.*;

import api.common.exception.fcm.FcmTokenNotFoundException;
import api.config.AcceptanceTestWithMongo;
import api.domain.fcm.business.FcmTokenBusiness;
import api.domain.fcm.controller.model.FcmTokenRequest;
import api.domain.fcm.service.FcmTokenService;
import api.domain.topic.business.TopicBusiness;
import api.domain.topic.controller.model.TopicSubscriptionRequest;
import db.domain.token.fcm.FcmTokenDocument;
import global.utils.NoticeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TopicOpenApiControllerTest extends AcceptanceTestWithMongo {

    @Autowired
    private TopicBusiness topicBusiness;

    @Autowired
    private FcmTokenBusiness fcmTokenBusiness;

    @Autowired
    private FcmTokenService fcmTokenService;

    @Test
    void 주제_구독_변경_성공() {

        // Given
        FcmTokenRequest fcmTokenRequest = new FcmTokenRequest();
        fcmTokenRequest.setFcmToken("my_test_token");
        fcmTokenBusiness.saveFcmToken(fcmTokenRequest);

        TopicSubscriptionRequest topicSubscriptionRequest = new TopicSubscriptionRequest();
        topicSubscriptionRequest.setFcmToken("my_test_token");
        topicSubscriptionRequest.setNoticeName(NoticeMapper.GENERAL_NEWS);
        topicSubscriptionRequest.setIsSubscribed(false);

        // When
        Boolean isRegistered = topicBusiness.subscribeTopic(topicSubscriptionRequest);

        // Then
        FcmTokenDocument fcmTokenDocument = fcmTokenService.getFcmTokenBy(fcmTokenRequest.getFcmToken());

        assertEquals(topicSubscriptionRequest.getIsSubscribed(),fcmTokenDocument.isGeneralNewsTopic());
        assertTrue(isRegistered);
    }

    @Test
    void 주제_구독_변경_실패() {

        // Given
        TopicSubscriptionRequest topicSubscriptionRequest = new TopicSubscriptionRequest();
        topicSubscriptionRequest.setFcmToken("my_test_tokene");
        topicSubscriptionRequest.setNoticeName(NoticeMapper.GENERAL_NEWS);
        topicSubscriptionRequest.setIsSubscribed(false);

        // When & Then
        assertThrows(FcmTokenNotFoundException.class, () -> topicBusiness.subscribeTopic(topicSubscriptionRequest));
    }


}