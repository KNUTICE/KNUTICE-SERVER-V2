package api.domain.fcm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import api.common.interceptor.AuthorizationInterceptor;
import api.domain.fcm.business.FcmTokenBusiness;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FcmTokenOpenApiController.class)
class FcmTokenOpenApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FcmTokenBusiness fcmTokenBusiness;

    @MockitoBean
    private AuthorizationInterceptor authorizationInterceptor;

//    @Test
    void 토큰_저장() throws Exception {
        String requestJson = """
                {
                    "body": {
                        "fcmToken": "fcmToken123"
                    }
                }
            """;

        mockMvc.perform(post("/open-api/fcm")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
        ).andExpect(status().isOk());
    }

}