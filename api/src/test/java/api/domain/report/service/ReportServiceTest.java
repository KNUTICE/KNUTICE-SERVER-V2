package api.domain.report.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import db.domain.report.ReportDocument;
import db.domain.report.ReportMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportMongoRepository reportMongoRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void submitReport() {
        // Given
        ReportDocument reportDocument = ReportDocument.builder()
            .id("123")
            .build();

        when(reportMongoRepository.save(reportDocument)).thenReturn(reportDocument);

        // When
        boolean result = reportService.submitReport(reportDocument);

        // Then
        assertThat(result).isTrue();
        verify(reportMongoRepository, times(1)).save(reportDocument);
    }

}