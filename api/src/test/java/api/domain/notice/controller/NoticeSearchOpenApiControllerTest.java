package api.domain.notice.controller;

import static org.junit.jupiter.api.Assertions.*;

import api.common.exception.notice.NoticeNotFoundException;
import api.config.AcceptanceTestWithMongo;
import api.domain.notice.business.NoticeSearchBusiness;
import api.domain.notice.controller.model.noticelist.NoticeResponse;
import api.domain.notice.controller.model.search.NoticeSearchRequest;
import api.init.NoticeSaveInitTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

class NoticeSearchOpenApiControllerTest extends AcceptanceTestWithMongo {

    @Autowired
    private NoticeSaveInitTest noticeSaveInitTest;

    @Autowired
    private NoticeSearchBusiness noticeSearchBusiness;

    @BeforeEach
    void setUp() {
        noticeSaveInitTest.initNotice();
    }

    @Test
    void 공지_검색_성공() {

        // Given
        NoticeSearchRequest noticeSearchRequest = new NoticeSearchRequest();
        noticeSearchRequest.setKeyword("테스트 title");

        // When
        List<NoticeResponse> searchResponse = noticeSearchBusiness.getSearchBy(noticeSearchRequest,
            Pageable.ofSize(40));

        // Then
        assertEquals(40, searchResponse.size());

    }

    @Test
    void 공지_검색_실패() {

        // Given
        NoticeSearchRequest noticeSearchRequest = new NoticeSearchRequest();
        noticeSearchRequest.setKeyword("내용 없음");

        // When & Then
        assertThrows(NoticeNotFoundException.class,
            () -> noticeSearchBusiness.getSearchBy(noticeSearchRequest, Pageable.ofSize(40)));
    }

}