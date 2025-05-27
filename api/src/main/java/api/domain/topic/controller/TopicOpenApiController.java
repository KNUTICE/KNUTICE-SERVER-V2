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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api/topic")
public class TopicOpenApiController {

    private final TopicBusiness topicBusiness;

    @PostMapping()
    public Api<Boolean> manageTopic(
        @RequestBody @Valid Api<TopicSubscriptionRequest> topicSubscriptionRequest
    ) {
        Boolean response = topicBusiness.subscribeTopic(topicSubscriptionRequest.getBody());
        return Api.OK(response);
    }

    @GetMapping()
    public Api<TopicStatusResponse> getTopicStatus(@RequestParam String fcmToken) {
        return Api.OK(topicBusiness.getTopicStatusBy(fcmToken));
    }

}

