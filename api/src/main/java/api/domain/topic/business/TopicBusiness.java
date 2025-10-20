package api.domain.topic.business;

import api.common.error.TopicErrorCode;
import api.common.exception.topic.TopicNotFoundException;
import api.domain.fcm.service.FcmTokenSeconService;
import api.domain.fcm.service.FcmTokenService;
import api.domain.topic.controller.model.TopicStatusResponse;
import api.domain.topic.controller.model.TopicSubscriptionRequest;
import api.domain.topic.converter.TopicConverter;
import api.infra.secon.FcmTokenSeconDocument;
import db.domain.token.fcm.FcmTokenDocument;
import global.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Business
@RequiredArgsConstructor
public class TopicBusiness {

    private final FcmTokenSeconService fcmTokenSeconService;

    private final TopicConverter topicConverter;

    public Boolean subscribeTopic(TopicSubscriptionRequest topicSubscriptionRequest) {

        FcmTokenSeconDocument fcmTokenDocument = fcmTokenSeconService.getFcmTokenBy(
            topicSubscriptionRequest.getFcmToken());

        fcmTokenDocument.updateNoticeTopicSubscription(topicSubscriptionRequest.getNoticeName(), topicSubscriptionRequest.getIsSubscribed());

        fcmTokenSeconService.saveFcmToken(fcmTokenDocument);
        log.info("구독 요청 토큰 : {}", topicSubscriptionRequest.getFcmToken());
        log.info("변경된 구독 상태 : [ {} : {} ]", topicSubscriptionRequest.getNoticeName(), topicSubscriptionRequest.getIsSubscribed());

        return true;
    }

    public TopicStatusResponse getTopicStatusBy(String fcmToken) {
        return topicConverter.toResponse(fcmTokenSeconService.getFcmTokenBy(fcmToken));
    }

}
