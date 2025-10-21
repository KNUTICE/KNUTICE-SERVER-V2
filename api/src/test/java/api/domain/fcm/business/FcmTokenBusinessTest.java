package api.domain.fcm.business;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import api.domain.fcm.controller.model.FcmTokenRequest;
import api.domain.fcm.converter.FcmTokenConverter;
import api.domain.fcm.service.FcmTokenService;
import db.domain.token.fcm.FcmTokenDocument;
import global.utils.DeviceType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FcmTokenBusinessTest {

    @Mock
    private FcmTokenService fcmTokenService;

    @Mock
    private FcmTokenConverter fcmTokenConverter;

    @InjectMocks
    private FcmTokenBusiness fcmTokenBusiness;

//    @Test
    void 토큰_존재_저장_성공() {
        // Given
        String fcmToken = "fcmToken123";
        FcmTokenRequest fcmTokenRequest = new FcmTokenRequest(fcmToken, DeviceType.iOS);

        LocalDateTime now = LocalDateTime.now();

        FcmTokenDocument existsFcmTokenDocument = FcmTokenDocument.builder()
            .fcmToken(fcmToken)
            .registeredAt(now)
            .failedCount(0)
            .build();

        when(fcmTokenService.getFcmToken(fcmTokenRequest.getFcmToken())).thenReturn(
            Optional.of(existsFcmTokenDocument));

        when(fcmTokenService.saveFcmToken(existsFcmTokenDocument)).thenReturn(true);

        // When
        Boolean result = fcmTokenBusiness.saveFcmToken(fcmTokenRequest);

        // Then
        assertThat(result).isTrue();
        assertThat(existsFcmTokenDocument.getFailedCount()).isEqualTo(0);
        assertThat(existsFcmTokenDocument.getRegisteredAt()).isNotNull();
        assertThat(existsFcmTokenDocument.getRegisteredAt()).isAfter(now);
    }

//    @Test
    void 토큰_없음_저장_성공() {
        // Given
        String fcmToken = "fcmToken123";
        FcmTokenRequest fcmTokenRequest = new FcmTokenRequest(fcmToken, DeviceType.iOS);

        FcmTokenDocument newFcmTokenDocument = FcmTokenDocument.builder()
            .fcmToken(fcmToken)
            .registeredAt(LocalDateTime.now())
            .build();

        when(fcmTokenService.getFcmToken(fcmTokenRequest.getFcmToken())).thenReturn(Optional.empty());
        when(fcmTokenConverter.toDocument(fcmTokenRequest)).thenReturn(newFcmTokenDocument);
        when(fcmTokenService.saveFcmToken(newFcmTokenDocument)).thenReturn(true);

        // When
        Boolean result = fcmTokenBusiness.saveFcmToken(fcmTokenRequest);

        // Then
        assertThat(result).isTrue();

        verify(fcmTokenService, times(1)).getFcmToken(fcmToken);
        verify(fcmTokenConverter, times(1)).toDocument(fcmTokenRequest);
        verify(fcmTokenService, times(1)).saveFcmToken(newFcmTokenDocument);
    }

}