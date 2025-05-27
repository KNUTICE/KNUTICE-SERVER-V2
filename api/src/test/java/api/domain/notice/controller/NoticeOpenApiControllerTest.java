package api.domain.notice.controller;

import static org.junit.jupiter.api.Assertions.*;

import api.common.exception.notice.NoticeNotFoundException;
import api.config.AcceptanceTestWithMongo;
import api.domain.notice.business.NoticeBusiness;
import api.domain.notice.controller.model.latestnotice.LatestThreeNoticeResponse;
import api.domain.notice.controller.model.noticelist.NoticeRequest;
import api.domain.notice.controller.model.noticelist.NoticeResponse;
import api.domain.notice.controller.model.sync.NoticeSyncRequest;
import api.init.NoticeSaveInitTest;
import global.utils.NoticeMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class NoticeOpenApiControllerTest extends AcceptanceTestWithMongo {

    @Autowired
    private NoticeSaveInitTest noticeSaveInitTest;

    @Autowired
    private NoticeBusiness noticeBusiness;

    @BeforeEach
    void setUp() {
        noticeSaveInitTest.initNotice();
    }

    @Test
    void 공지_목록_조회_성공() {
        // Given
        NoticeRequest noticeRequest = new NoticeRequest();
        noticeRequest.setNoticeName(NoticeMapper.GENERAL_NEWS);

        // When
        List<NoticeResponse> noticeList = noticeBusiness.getNoticeList(noticeRequest,
            Pageable.ofSize(10));

        // Then
        assertEquals(10, noticeList.size());
    }

    @Test
    void 메인_페이지_조회_성공() {
        // When
        LatestThreeNoticeResponse latestThreeNotice = noticeBusiness.getLatestThreeNotice();

        // Then
        assertEquals(3, latestThreeNotice.getLatestThreeGeneralNews().size());
        assertEquals(3, latestThreeNotice.getLatestThreeAcademicNews().size());
        assertEquals(3, latestThreeNotice.getLatestThreeEventNews().size());
        assertEquals(3, latestThreeNotice.getLatestThreeScholarshipNews().size());
    }

    @Test
    void 단일_공지_조회_성공() {
        // When
        NoticeResponse noticeResponse = noticeBusiness.getNoticeBy(1L);

        // Then
        assertEquals(NoticeMapper.GENERAL_NEWS, noticeResponse.getNoticeName());
        assertEquals("GENERAL_NEWS 테스트 title 1", noticeResponse.getTitle());
    }

    @Test
    void 단일_공지_조회_실패() {

        // When & Then
        assertThrows(NoticeNotFoundException.class, () -> noticeBusiness.getNoticeBy(100L));
    }

    @Test
    void 조회_withNttIdList_성공() {
        // Given
        NoticeSyncRequest noticeSyncRequest = new NoticeSyncRequest();
        noticeSyncRequest.setNttIdList(List.of(1L, 2L, 3L));

        // When
        List<NoticeResponse> noticeList = noticeBusiness.getNoticeList(noticeSyncRequest);

        // Then
        assertEquals(3, noticeList.size());
    }

}
