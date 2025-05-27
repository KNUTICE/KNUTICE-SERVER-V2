package api.domain.report.business;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import api.common.error.FcmTokenErrorCode;
import api.common.exception.fcm.FcmTokenNotFoundException;
import api.domain.fcm.service.FcmTokenService;
import api.domain.report.controller.model.ReportRequest;
import api.domain.report.converter.ReportConverter;
import api.domain.report.service.ReportService;
import db.domain.report.ReportDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportBusinessTest {

    @Mock
    private ReportService reportService;

    @Mock
    private FcmTokenService fcmTokenService;

    @Mock
    private ReportConverter reportConverter;

    @InjectMocks
    private ReportBusiness reportBusiness;

    @Test
    void submitReport_success() {
        // Given
        String fcmToken = "fcmToken123";
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setFcmToken(fcmToken);

        ReportDocument reportDocument = ReportDocument.builder()
            .fcmToken(fcmToken)
            .build();

        when(fcmTokenService.existsBy(reportRequest.getFcmToken())).thenReturn(true);
        when(reportConverter.toDocument(reportRequest)).thenReturn(reportDocument);
        when(reportService.submitReport(reportDocument)).thenReturn(true);

        // When
        Boolean result = reportBusiness.submitReport(reportRequest);

        // Then
        assertThat(result).isTrue();
        verify(fcmTokenService).existsBy(fcmToken);
        verify(reportConverter).toDocument(reportRequest);
        verify(reportService).submitReport(reportDocument);
    }

    @Test
    void submitReport_throwsException_whenFcmTokenNotFound() {
        // given
        String fcmToken = "fcmToken123";
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setFcmToken(fcmToken);

        when(fcmTokenService.existsBy(fcmToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> reportBusiness.submitReport(reportRequest))
            .isInstanceOf(FcmTokenNotFoundException.class)
            .hasMessageContaining(FcmTokenErrorCode.TOKEN_NOT_FOUND.getDescription());

        verify(fcmTokenService).existsBy(fcmToken);
        verifyNoInteractions(reportConverter, reportService);
    }

}
