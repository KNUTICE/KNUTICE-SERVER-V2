package api.domain.topic.business;

import api.common.error.TopicErrorCode;
import api.common.exception.topic.TopicNotFoundException;
import api.domain.fcm.service.FcmTokenService;
import api.domain.topic.controller.model.TopicStatusResponse;
import api.domain.topic.controller.model.TopicSubscriptionRequest;
import api.domain.topic.converter.TopicConverter;
import db.domain.token.fcm.FcmTokenDocument;
import global.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Business
@RequiredArgsConstructor
public class TopicBusiness {

    private final FcmTokenService fcmTokenService;
    private final TopicConverter topicConverter;

    public Boolean subscribeTopic(TopicSubscriptionRequest topicSubscriptionRequest) {

        FcmTokenDocument fcmTokenDocument = fcmTokenService.getFcmTokenBy(
            topicSubscriptionRequest.getFcmToken());

        setTopic(topicSubscriptionRequest, fcmTokenDocument);

        fcmTokenService.saveFcmToken(fcmTokenDocument);
        log.info("구독 요청 토큰 : {}", topicSubscriptionRequest.getFcmToken());
        log.info("변경된 구독 상태 : [ {} : {} ]", topicSubscriptionRequest.getNoticeName(), topicSubscriptionRequest.getIsSubscribed());

        return true;
    }

    public TopicStatusResponse getTopicStatusBy(String fcmToken) {
        return topicConverter.toResponse(fcmTokenService.getFcmTokenBy(fcmToken));
    }

    private static void setTopic(TopicSubscriptionRequest topicSubscriptionRequest, FcmTokenDocument fcmTokenDocument) {
        switch (topicSubscriptionRequest.getNoticeName()) {
            case GENERAL_NEWS:
                fcmTokenDocument.setGeneralNewsTopic(topicSubscriptionRequest.getIsSubscribed());
                break;
            case SCHOLARSHIP_NEWS:
                fcmTokenDocument.setScholarshipNewsTopic(topicSubscriptionRequest.getIsSubscribed());
                break;
            case EVENT_NEWS:
                fcmTokenDocument.setEventNewsTopic(topicSubscriptionRequest.getIsSubscribed());
                break;
            case ACADEMIC_NEWS:
                fcmTokenDocument.setAcademicNewsTopic(topicSubscriptionRequest.getIsSubscribed());
                break;
            case EMPLOYMENT_NEWS:
                fcmTokenDocument.setEmploymentNewsTopic(topicSubscriptionRequest.getIsSubscribed());
                break;
            default:
                throw new TopicNotFoundException(TopicErrorCode.TOPIC_NOT_FOUND);
        }
    }

}
