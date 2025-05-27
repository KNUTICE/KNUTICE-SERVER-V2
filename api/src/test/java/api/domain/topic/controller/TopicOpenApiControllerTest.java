package api.domain.topic.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

import api.common.interceptor.AuthorizationInterceptor;
import api.domain.topic.business.TopicBusiness;
import api.domain.topic.controller.model.TopicSubscriptionRequest;
import global.api.Api;
import global.utils.NoticeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TopicOpenApiController.class)
class TopicOpenApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TopicBusiness topicBusiness;

    @MockitoBean
    private AuthorizationInterceptor authorizationInterceptor;

    @Test
    void manageTopic() throws Exception {
        // given
        String requestJson = """
                {
                    "body": {
                        "fcmToken": "fcmToken123",
                        "noticeName": "GENERAL_NEWS",
                        "isSubscribed": false
                    }
                }
            """;

        mockMvc.perform(post("/open-api/topic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk());

        verify(topicBusiness).subscribeTopic(any(TopicSubscriptionRequest.class));
    }

    @Test
    void getTopicStatus() throws Exception {
        String fcmToken = "fcmToken123";
        mockMvc.perform(get("/open-api/topic")
                .param("fcmToken", fcmToken)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

}