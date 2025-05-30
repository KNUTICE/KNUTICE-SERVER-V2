package api.domain.fcm.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import api.common.exception.fcm.FcmTokenNotFoundException;
import db.domain.token.fcm.FcmTokenDocument;
import db.domain.token.fcm.FcmTokenMongoRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FcmTokenServiceTest {

    @Mock
    private FcmTokenMongoRepository fcmTokenMongoRepository;

    @InjectMocks
    private FcmTokenService fcmTokenService;

    @Test
    void 토큰_조회_성공() {
        // Given
        String fcmToken = "fcmToken123";
        FcmTokenDocument fcmTokenDocument = FcmTokenDocument.builder()
            .fcmToken(fcmToken)
            .build();

        when(fcmTokenMongoRepository.findById(fcmToken)).thenReturn(Optional.of(fcmTokenDocument));

        // When
        Optional<FcmTokenDocument> result = fcmTokenService.getFcmToken(fcmToken);

        // Then
        verify(fcmTokenMongoRepository, times(1)).findById(fcmToken);

        assertThat(result).isPresent();
        assertThat(result.get().getFcmToken()).isEqualTo(fcmToken);
    }

    @Test
    void 토큰_저장_성공() {
        // Given
        String fcmToken = "fcmToken123";
        FcmTokenDocument fcmTokenDocument = FcmTokenDocument.builder()
            .fcmToken(fcmToken)
            .build();

        when(fcmTokenMongoRepository.save(fcmTokenDocument)).thenReturn(fcmTokenDocument);

        // When
        Boolean result = fcmTokenService.saveFcmToken(fcmTokenDocument);

        // Then
        verify(fcmTokenMongoRepository, times(1)).save(fcmTokenDocument);

        assertThat(result).isTrue();
    }

    @Test
    void ID로_토큰_조회_성공() {
        // Given
        String fcmToken = "fcmToken123";
        FcmTokenDocument fcmTokenDocument = FcmTokenDocument.builder()
            .fcmToken(fcmToken)
            .build();

        when(fcmTokenMongoRepository.findById(fcmToken)).thenReturn(Optional.of(fcmTokenDocument));

        // When
        FcmTokenDocument result = fcmTokenService.getFcmTokenBy(fcmToken);

        // Then
        verify(fcmTokenMongoRepository, times(1)).findById(fcmToken);

        assertThat(result).isEqualTo(fcmTokenDocument);
    }

    @Test
    void 토큰_조회_실패() {
        // Given
        String fcmToken = "fcmToken123";

        when(fcmTokenMongoRepository.findById(fcmToken)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> fcmTokenService.getFcmTokenBy(fcmToken))
            .isInstanceOf(FcmTokenNotFoundException.class);
    }

    @Test
    void 토큰_유무_성공() {
        // Given
        String fcmToken = "fcmToken123";

        when(fcmTokenMongoRepository.existsById(fcmToken)).thenReturn(true);

        // When
        boolean result = fcmTokenMongoRepository.existsById(fcmToken);

        // Then
        assertThat(result).isTrue();
    }

}