package api.domain.topic.business;

import api.domain.token.service.TokenService;
import api.domain.topic.controller.model.TopicSubscriptionRequest;
import db.domain.token.DeviceTokenDocument;
import global.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Business
@RequiredArgsConstructor
public class TopicBusiness {

    private final TokenService tokenService;

    public Boolean subscribeTopic(TopicSubscriptionRequest topicSubscriptionRequest) {

        DeviceTokenDocument deviceTokenDocument = tokenService.getDeviceTokenBy(
            topicSubscriptionRequest.getDeviceToken());

        setTopic(topicSubscriptionRequest, deviceTokenDocument);

        tokenService.saveDeviceToken(deviceTokenDocument);
        log.info("구독 요청 토큰 : {}", topicSubscriptionRequest.getDeviceToken());
        log.info("변경된 구독 상태 : [ {} : {} ]", topicSubscriptionRequest.getNoticeName(), topicSubscriptionRequest.getIsSubscribed());

        return true;
    }

    private static void setTopic(TopicSubscriptionRequest topicSubscriptionRequest, DeviceTokenDocument deviceTokenDocument) {
        switch (topicSubscriptionRequest.getNoticeName()) {
            case GENERAL_NEWS:
                deviceTokenDocument.setGeneralNewsTopic(topicSubscriptionRequest.getIsSubscribed());
                break;
            case SCHOLARSHIP_NEWS:
                deviceTokenDocument.setScholarshipNewsTopic(topicSubscriptionRequest.getIsSubscribed());
                break;
            case EVENT_NEWS:
                deviceTokenDocument.setEventNewsTopic(topicSubscriptionRequest.getIsSubscribed());
                break;
            case ACADEMIC_NEWS:
                deviceTokenDocument.setAcademicNewsTopic(topicSubscriptionRequest.getIsSubscribed());
                break;
            default:
                throw new RuntimeException("예외"); // TODO 예외
        }
    }

}
