package api.domain.notice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import api.common.interceptor.AuthorizationInterceptor;
import api.domain.notice.business.NoticeBusiness;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NoticeOpenApiController.class)
class NoticeOpenApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoticeBusiness noticeBusiness;

    @MockitoBean
    private AuthorizationInterceptor authorizationInterceptor;

    @Test
    void getNoticeList() throws Exception {
        mockMvc.perform(get("/open-api/notice/list")
            .contentType(MediaType.APPLICATION_JSON)
            .param("noticeName", "GENERAL_NEWS")
            .param("nttId", "1234")
        ).andExpect(status().isOk());
    }

    @Test
    void getLatestThreeNotice() throws Exception {
        mockMvc.perform(get("/open-api/notice")
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void getNoticeById() throws Exception {
        Long nttId = 1234L;
        mockMvc.perform(get("/open-api/notice/{nttId}", nttId))
            .andExpect(status().isOk());
    }

    @Test
    void getNotice() throws Exception {
        String requestJson = """
            {
              "body": {
                "nttIdList": [1, 2, 3]
              }
            }
        """;
        mockMvc.perform(post("/open-api/notice/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk());
    }

}