package api.domain.report.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import api.common.interceptor.AuthorizationInterceptor;
import api.domain.report.business.ReportBusiness;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReportOpenApiController.class)
class ReportOpenApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportBusiness reportBusiness;

    @MockitoBean
    private AuthorizationInterceptor authorizationInterceptor;

    @Test
    void submitReport() throws Exception {
        String requestJson = """
                {
                    "body": {
                        "fcmToken": "fcmToken123",
                        "content": "알림이 전송되지 않습니다.",
                        "clientType": "APP",
                        "deviceName": "iOS15",
                        "version": "1.2.1"
                    }
                }
            """;

        mockMvc.perform(post("/open-api/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk());
    }

}