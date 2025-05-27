package api.domain.urgent.controller;

import static org.junit.jupiter.api.Assertions.*;

import api.config.AcceptanceTestWithMongo;
import api.domain.admin.service.AdminService;
import api.domain.urgent.business.UrgentNoticeBusiness;
import api.domain.urgent.controller.model.UrgentNoticeResponse;
import db.domain.urgent.UrgentNoticeDocument;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UrgentNoticeOpenApiControllerTest extends AcceptanceTestWithMongo {

    @Autowired
    private UrgentNoticeBusiness urgentNoticeBusiness;

    @Autowired
    private AdminService adminService;

    @Test
    void 긴급공지_조회_성공() {

        // Given
        UrgentNoticeDocument urgentNoticeDocument = UrgentNoticeDocument.builder()
            .title("테스트")
            .content("내용")
            .contentUrl("URL")
            .registeredAt(LocalDate.now())
            .build();
        adminService.saveUrgentNoticeBy(urgentNoticeDocument);

        // When
        UrgentNoticeResponse urgentNotice = urgentNoticeBusiness.getUrgentNotice();

        // Then
        assertEquals(urgentNoticeDocument.getTitle(), urgentNotice.getTitle());
        assertEquals(urgentNoticeDocument.getContent(), urgentNotice.getContent());
        assertEquals(urgentNoticeDocument.getContentUrl(), urgentNotice.getContentUrl());
    }
}