package api.domain.notice.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import db.domain.notice.NoticeDocument;
import db.domain.notice.NoticeMongoRepository;
import db.domain.notice.NoticeQueryRepository;
import db.domain.notice.dto.QNoticeDto;
import db.domain.notice.dto.QNoticeSearchDto;
import global.utils.NoticeMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeQueryRepository noticeQueryRepository;

    @Mock
    private NoticeMongoRepository noticeMongoRepository;

    @InjectMocks
    private NoticeService noticeService;

    @Test
    void getNoticeList() {
        // Given
        QNoticeDto qNoticeDto = QNoticeDto.builder()
            .noticeName(NoticeMapper.GENERAL_NEWS)
            .nttId(1L)
            .page(Pageable.ofSize(1))
            .build();

        List<NoticeDocument> dummyList = List.of(new NoticeDocument());

        when(noticeQueryRepository.findNoticeBy(qNoticeDto)).thenReturn(dummyList);

        // When
        List<NoticeDocument> result = noticeService.getNoticeList(qNoticeDto);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        verify(noticeQueryRepository).findNoticeBy(qNoticeDto);
    }

    @Test
    void getLatestThreeNoticeBy() {
        // Given
        NoticeMapper mapper = NoticeMapper.GENERAL_NEWS;
        List<NoticeDocument> dummyList = List.of(new NoticeDocument(), new NoticeDocument(), new NoticeDocument());

        when(noticeQueryRepository.findLatestThreeNoticeBy(mapper)).thenReturn(dummyList);

        // When
        List<NoticeDocument> result = noticeService.getLatestThreeNoticeBy(mapper);

        // Then
        assertThat(result).hasSize(3);
        verify(noticeQueryRepository).findLatestThreeNoticeBy(mapper);
    }


    @Test
    void getNoticeSearchList() {
        // Given
        QNoticeSearchDto qNoticeSearchDto = QNoticeSearchDto.builder().build();
        List<NoticeDocument> dummyList = List.of(new NoticeDocument());

        when(noticeQueryRepository.findSearchBy(qNoticeSearchDto)).thenReturn(dummyList);

        // When
        List<NoticeDocument> result = noticeService.getNoticeSearchList(qNoticeSearchDto);

        // Then
        assertThat(result).hasSize(1);
        verify(noticeQueryRepository).findSearchBy(qNoticeSearchDto);
    }

    @Test
    void getNoticeBy() {
        // Given
        Long nttId = 1L;
        NoticeDocument document = new NoticeDocument();

        when(noticeMongoRepository.findById(nttId)).thenReturn(Optional.of(document));

        // When
        NoticeDocument result = noticeService.getNoticeBy(nttId);

        // Then
        assertThat(result).isEqualTo(document);
        verify(noticeMongoRepository).findById(nttId);
    }

    @Test
    void existsNoticeBy() {
        // Given
        Long nttId = 1L;
        when(noticeMongoRepository.existsById(nttId)).thenReturn(true);

        // When
        Boolean exists = noticeService.existsNoticeBy(nttId);

        // Then
        assertThat(exists).isTrue();
        verify(noticeMongoRepository).existsById(nttId);
    }

}