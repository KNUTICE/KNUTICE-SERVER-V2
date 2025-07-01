package api.domain.topic.controller;

import api.domain.topic.business.TopicBusiness;
import api.domain.topic.controller.model.TopicStatusResponse;
import api.domain.topic.controller.model.TopicSubscriptionRequest;
import global.api.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/open-api/topic", "/open-api/topics"})
public class TopicOpenApiController {

    private final TopicBusiness topicBusiness;

    @PostMapping({"", "/subscription"})
    public Api<Boolean> manageTopic(
        @RequestBody @Valid Api<TopicSubscriptionRequest> topicSubscriptionRequest
    ) {
        Boolean response = topicBusiness.subscribeTopic(topicSubscriptionRequest.getBody());
        return Api.OK(response);
    }

    @GetMapping({"", "/status"})
    public Api<TopicStatusResponse> getTopicStatus(@RequestHeader String fcmToken) {
        return Api.OK(topicBusiness.getTopicStatusBy(fcmToken));
    }

}

