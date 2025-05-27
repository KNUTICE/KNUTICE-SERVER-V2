package api.domain.urgent.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import api.domain.urgent.controller.model.UrgentNoticeResponse;
import api.domain.urgent.converter.UrgentNoticeConverter;
import api.domain.urgent.service.UrgentNoticeService;
import db.domain.urgent.UrgentNoticeDocument;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrgentNoticeBusinessTest {

    @Mock
    private UrgentNoticeService urgentNoticeService;

    @Mock
    private UrgentNoticeConverter urgentNoticeConverter;

    @InjectMocks
    private UrgentNoticeBusiness urgentNoticeBusiness;

    @Test
    void getUrgentNotice() {
        // Given
        UrgentNoticeDocument urgentNoticeDocument = UrgentNoticeDocument.builder()
            .id("1")
            .title("긴급 공지")
            .content("긴급 공지 내용")
            .contentUrl("https://example.com")
            .registeredAt(LocalDate.now())
            .build();

        UrgentNoticeResponse urgentNoticeResponse = UrgentNoticeResponse.builder()
            .title("긴급 공지")
            .content("긴급 공지 내용")
            .contentUrl("https://example.com")
            .registeredAt(urgentNoticeDocument.getRegisteredAt())
            .build();

        when(urgentNoticeService.getUrgentNoticeList()).thenReturn(List.of(urgentNoticeDocument));
        when(urgentNoticeConverter.toResponse(urgentNoticeDocument)).thenReturn(urgentNoticeResponse);

        // When
        UrgentNoticeResponse result = urgentNoticeBusiness.getUrgentNotice();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("긴급 공지");
        assertThat(result.getContent()).isEqualTo("긴급 공지 내용");
        assertThat(result.getContentUrl()).isEqualTo("https://example.com");

        verify(urgentNoticeService, times(1)).getUrgentNoticeList();
        verify(urgentNoticeConverter, times(1)).toResponse(urgentNoticeDocument);
    }

}
