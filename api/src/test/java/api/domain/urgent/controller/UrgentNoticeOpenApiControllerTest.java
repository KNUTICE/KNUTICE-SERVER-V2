package api.domain.urgent.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import api.common.interceptor.AuthorizationInterceptor;
import api.domain.urgent.business.UrgentNoticeBusiness;
import api.domain.urgent.controller.model.UrgentNoticeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UrgentNoticeOpenApiController.class)
class UrgentNoticeOpenApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrgentNoticeBusiness urgentNoticeBusiness;

    @MockitoBean
    private AuthorizationInterceptor authorizationInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
    void getUrgentNotice_성공적으로_조회함() throws Exception {
        // Given
        UrgentNoticeResponse response = UrgentNoticeResponse.builder()
            .title("긴급 공지")
            .content("긴급 공지 내용")
            .contentUrl("https://example.com")
            .registeredAt(LocalDate.of(2024, 5, 1))
            .build();

        when(urgentNoticeBusiness.getUrgentNotice()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/open-api/urgent"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.title").value("긴급 공지"))
            .andExpect(jsonPath("$.body.content").value("긴급 공지 내용"))
            .andExpect(jsonPath("$.body.contentUrl").value("https://example.com"))
            .andExpect(jsonPath("$.body.registeredAt").value("2024-05-01"));

        verify(urgentNoticeBusiness, times(1)).getUrgentNotice();
    }

}