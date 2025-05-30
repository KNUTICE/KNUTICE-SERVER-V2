
package api.domain.notice.business;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import api.domain.notice.controller.model.latestnotice.LatestThreeNoticeResponse;
import api.domain.notice.controller.model.noticelist.NoticeRequest;
import api.domain.notice.controller.model.noticelist.NoticeResponse;
import api.domain.notice.controller.model.sync.NoticeSyncRequest;
import api.domain.notice.converter.NoticeConverter;
import api.domain.notice.service.NoticeService;
import db.domain.notice.NoticeDocument;
import db.domain.notice.dto.QNoticeDto;
import global.utils.NoticeMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NoticeBusinessTest {

    @Mock
    private NoticeService noticeService;

    @Mock
    private NoticeConverter noticeConverter;

    @InjectMocks
    private NoticeBusiness noticeBusiness;

    @Test
    void getNoticeList() {
        // Given
        NoticeRequest noticeRequest = new NoticeRequest(NoticeMapper.GENERAL_NEWS, 1L);
        Pageable page = PageRequest.of(0, 10);

        QNoticeDto qNoticeDto = QNoticeDto.builder()
            .noticeName(noticeRequest.getNoticeName())
            .nttId(noticeRequest.getNttId())
            .page(page)
            .build();

        NoticeDocument document = NoticeDocument.builder()
            .nttId(1L)
            .noticeName(NoticeMapper.GENERAL_NEWS)
            .title("제목")
            .contentUrl("http://content.url")
            .contentImage("http://image.url")
            .departmentName("부서")
            .registeredAt("2025-01-01")
            .build();

        List<NoticeDocument> dummyList = List.of(document);
        List<NoticeResponse> dummyResponseList = List.of(
            NoticeResponse.builder()
                .nttId(1L)
                .title("제목")
                .contentUrl("http://content.url")
                .departmentName("부서")
                .build()
        );

        when(noticeConverter.toDto(noticeRequest, page)).thenReturn(qNoticeDto);
        when(noticeService.getNoticeList(qNoticeDto)).thenReturn(dummyList);
        when(noticeConverter.toResponse(dummyList)).thenReturn(dummyResponseList);

        // When
        List<NoticeResponse> result = noticeBusiness.getNoticeList(noticeRequest, page);

        // Then
        assertThat(result).hasSize(1);
        NoticeResponse response = result.get(0);
        assertThat(response.getNttId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("제목");
        assertThat(response.getContentUrl()).isEqualTo("http://content.url");
        assertThat(response.getDepartmentName()).isEqualTo("부서");

        verify(noticeConverter).toDto(noticeRequest, page);
        verify(noticeService).getNoticeList(qNoticeDto);
        verify(noticeConverter).toResponse(dummyList);
    }

    @Test
    void getLatestThreeNotice() {
        // Given
        List<NoticeDocument> generalNews = List.of(
            NoticeDocument.builder().nttId(1L).noticeName(NoticeMapper.GENERAL_NEWS).title("일반 공지 1").build()
        );
        List<NoticeDocument> scholarshipNews = List.of(
            NoticeDocument.builder().nttId(2L).noticeName(NoticeMapper.SCHOLARSHIP_NEWS).title("장학 공지 1").build()
        );
        List<NoticeDocument> eventNews = List.of(
            NoticeDocument.builder().nttId(3L).noticeName(NoticeMapper.EVENT_NEWS).title("행사 공지 1").build()
        );
        List<NoticeDocument> academicNews = List.of(
            NoticeDocument.builder().nttId(4L).noticeName(NoticeMapper.ACADEMIC_NEWS).title("학사 공지 1").build()
        );
        List<NoticeDocument> employmentNews = List.of(
            NoticeDocument.builder().nttId(5L).noticeName(NoticeMapper.EMPLOYMENT_NEWS).title("취업 공지 1").build()
        );

        LatestThreeNoticeResponse dummyResponse = LatestThreeNoticeResponse.builder()
            .latestThreeGeneralNews(List.of(
                NoticeResponse.builder()
                    .nttId(1L)
                    .title("일반 공지 1")
                    .noticeName(NoticeMapper.GENERAL_NEWS)
                    .build()
            ))
            .latestThreeScholarshipNews(List.of(
                NoticeResponse.builder()
                    .nttId(2L)
                    .title("장학 공지 1")
                    .noticeName(NoticeMapper.SCHOLARSHIP_NEWS)
                    .build()
            ))
            .latestThreeEventNews(List.of(
                NoticeResponse.builder()
                    .nttId(3L)
                    .title("행사 공지 1")
                    .noticeName(NoticeMapper.EVENT_NEWS)
                    .build()
            ))
            .latestThreeAcademicNews(List.of(
                NoticeResponse.builder()
                    .nttId(4L)
                    .title("학사 공지 1")
                    .noticeName(NoticeMapper.ACADEMIC_NEWS)
                    .build()
            ))
            .latestThreeEmploymentNews(List.of(
                NoticeResponse.builder()
                    .nttId(5L)
                    .title("취업 공지 1")
                    .noticeName(NoticeMapper.EMPLOYMENT_NEWS)
                    .build()
            ))
            .build();

        // Mocking
        when(noticeService.getLatestThreeNoticeBy(NoticeMapper.GENERAL_NEWS)).thenReturn(generalNews);
        when(noticeService.getLatestThreeNoticeBy(NoticeMapper.SCHOLARSHIP_NEWS)).thenReturn(scholarshipNews);
        when(noticeService.getLatestThreeNoticeBy(NoticeMapper.EVENT_NEWS)).thenReturn(eventNews);
        when(noticeService.getLatestThreeNoticeBy(NoticeMapper.ACADEMIC_NEWS)).thenReturn(academicNews);
        when(noticeService.getLatestThreeNoticeBy(NoticeMapper.EMPLOYMENT_NEWS)).thenReturn(employmentNews);

        when(noticeConverter.toResponse(generalNews, scholarshipNews, eventNews, academicNews, employmentNews))
            .thenReturn(dummyResponse);

        // When
        LatestThreeNoticeResponse result = noticeBusiness.getLatestThreeNotice();

        // Then
        assertThat(result.getLatestThreeGeneralNews()).hasSize(1);
        assertThat(result.getLatestThreeGeneralNews().get(0).getTitle()).isEqualTo("일반 공지 1");
        assertThat(result.getLatestThreeGeneralNews().get(0).getNoticeName()).isEqualTo(NoticeMapper.GENERAL_NEWS);

        assertThat(result.getLatestThreeScholarshipNews().get(0).getTitle()).isEqualTo("장학 공지 1");
        assertThat(result.getLatestThreeEventNews().get(0).getTitle()).isEqualTo("행사 공지 1");
        assertThat(result.getLatestThreeAcademicNews().get(0).getTitle()).isEqualTo("학사 공지 1");
        assertThat(result.getLatestThreeEmploymentNews().get(0).getTitle()).isEqualTo("취업 공지 1");

        verify(noticeService).getLatestThreeNoticeBy(NoticeMapper.GENERAL_NEWS);
        verify(noticeService).getLatestThreeNoticeBy(NoticeMapper.SCHOLARSHIP_NEWS);
        verify(noticeService).getLatestThreeNoticeBy(NoticeMapper.EVENT_NEWS);
        verify(noticeService).getLatestThreeNoticeBy(NoticeMapper.ACADEMIC_NEWS);
        verify(noticeService).getLatestThreeNoticeBy(NoticeMapper.EMPLOYMENT_NEWS);
        verify(noticeConverter).toResponse(generalNews, scholarshipNews, eventNews, academicNews, employmentNews);
    }

    @Test
    void getNoticeBy() {
        // Given
        Long nttId = 1L;
        NoticeDocument noticeDocument = NoticeDocument.builder()
            .nttId(nttId)
            .noticeName(NoticeMapper.GENERAL_NEWS)
            .build();

        NoticeResponse noticeResponse = NoticeResponse.builder()
            .nttId(nttId)
            .noticeName(NoticeMapper.GENERAL_NEWS)
            .build();

        when(noticeService.getNoticeBy(nttId)).thenReturn(noticeDocument);
        when(noticeConverter.toResponse(noticeDocument)).thenReturn(noticeResponse);

        // When
        NoticeResponse result = noticeBusiness.getNoticeBy(nttId);

        // Then
        assertThat(result.getNttId()).isEqualTo(noticeDocument.getNttId());
        assertThat(result.getNoticeName()).isEqualTo(noticeDocument.getNoticeName());

        verify(noticeService).getNoticeBy(nttId);
        verify(noticeConverter).toResponse(noticeDocument);
    }

    @Test
    void getNoticeListWithSyncRequest() {
        // Given
        List<Long> nttIdList = List.of(101L, 102L);
        NoticeSyncRequest syncRequest = new NoticeSyncRequest(nttIdList);

        NoticeDocument doc1 = NoticeDocument.builder()
            .nttId(101L)
            .title("공지사항 1")
            .noticeName(NoticeMapper.GENERAL_NEWS)
            .build();

        NoticeDocument doc2 = NoticeDocument.builder()
            .nttId(102L)
            .title("공지사항 2")
            .noticeName(NoticeMapper.SCHOLARSHIP_NEWS)
            .build();

        List<NoticeDocument> documents = List.of(doc1, doc2);

        NoticeResponse response1 = NoticeResponse.builder()
            .nttId(101L)
            .title("공지사항 1")
            .registeredAt("2025-01-01")
            .noticeName(NoticeMapper.GENERAL_NEWS)
            .build();

        NoticeResponse response2 = NoticeResponse.builder()
            .nttId(102L)
            .title("공지사항 2")
            .registeredAt("2025-01-02")
            .noticeName(NoticeMapper.SCHOLARSHIP_NEWS)
            .build();

        List<NoticeResponse> expectedResponse = List.of(response1, response2);

        // Mocking
        when(noticeService.getNoticeList(nttIdList)).thenReturn(documents);
        when(noticeConverter.toResponse(documents)).thenReturn(expectedResponse);

        // When
        List<NoticeResponse> result = noticeBusiness.getNoticeList(syncRequest);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("nttId").containsExactly(101L, 102L);
        assertThat(result.get(0).getTitle()).isEqualTo("공지사항 1");
        assertThat(result.get(1).getNoticeName()).isEqualTo(NoticeMapper.SCHOLARSHIP_NEWS);

        verify(noticeService).getNoticeList(nttIdList);
        verify(noticeConverter).toResponse(documents);
    }

}
