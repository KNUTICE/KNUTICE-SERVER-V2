package api.domain.urgent.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import api.common.exception.urgent.UrgentNoticeNotFoundException;
import db.domain.urgent.UrgentNoticeDocument;
import db.domain.urgent.UrgentNoticeMongoRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrgentNoticeServiceTest {

    @Mock
    private UrgentNoticeMongoRepository urgentNoticeMongoRepository;

    @InjectMocks
    private UrgentNoticeService urgentNoticeService;

    @Test
    void getUrgentNoticeList_success() {
        // Given
        UrgentNoticeDocument urgentNoticeDocument = UrgentNoticeDocument.builder()
            .id("123")
            .title("제목")
            .build();

        List<UrgentNoticeDocument> urgentNoticeDocumentList = List.of(urgentNoticeDocument);

        when(urgentNoticeMongoRepository.findAll()).thenReturn(urgentNoticeDocumentList);

        // When
        List<UrgentNoticeDocument> urgentNoticeList = urgentNoticeService.getUrgentNoticeList();

        // Then
        assertThat(urgentNoticeList).hasSize(1);
        assertThat(urgentNoticeList.get(0).getId()).isEqualTo("123");
        assertThat(urgentNoticeList.get(0).getTitle()).isEqualTo("제목");

    }

    @Test
    void getUrgentNoticeList_fail() {
        // Given
        when(urgentNoticeMongoRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> urgentNoticeService.getUrgentNoticeList())
            .isInstanceOf(UrgentNoticeNotFoundException.class);
    }

}
