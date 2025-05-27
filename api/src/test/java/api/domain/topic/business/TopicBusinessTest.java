package api.domain.topic.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import api.domain.fcm.service.FcmTokenService;
import api.domain.topic.controller.model.TopicStatusResponse;
import api.domain.topic.controller.model.TopicSubscriptionRequest;
import api.domain.topic.converter.TopicConverter;
import db.domain.token.fcm.FcmTokenDocument;
import global.utils.NoticeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TopicBusinessTest {

    @Mock
    private FcmTokenService fcmTokenService;

    @Mock
    private TopicConverter topicConverter;

    @InjectMocks
    private TopicBusiness topicBusiness;

    @Test
    void subscribeTopic() {
        // Given
        String fcmToken = "fcmToken123";

        TopicSubscriptionRequest topicRequest = new TopicSubscriptionRequest();
        topicRequest.setFcmToken(fcmToken);
        topicRequest.setNoticeName(NoticeMapper.GENERAL_NEWS);
        topicRequest.setIsSubscribed(false);

        FcmTokenDocument fcmTokenDocument = FcmTokenDocument.builder()
            .fcmToken(fcmToken)
            .generalNewsTopic(true)
            .scholarshipNewsTopic(true)
            .eventNewsTopic(true)
            .academicNewsTopic(true)
            .employmentNewsTopic(true)
            .build();

        when(fcmTokenService.getFcmTokenBy(fcmToken)).thenReturn(fcmTokenDocument);

        // When
        Boolean result = topicBusiness.subscribeTopic(topicRequest);

        // Then
        assertThat(result).isTrue();
        assertThat(fcmTokenDocument.isGeneralNewsTopic()).isFalse(); // False 로 변경

        verify(fcmTokenService).getFcmTokenBy(fcmToken);
        verify(fcmTokenService).saveFcmToken(fcmTokenDocument);
        verifyNoMoreInteractions(fcmTokenService);
    }

    @Test
    void getTopicStatusBy() {
        // Given
        String fcmToken = "fcmToken123";
        FcmTokenDocument fcmTokenDocument = FcmTokenDocument.builder()
            .fcmToken(fcmToken)
            .build();

        TopicStatusResponse topicStatusResponse = TopicStatusResponse.builder()
            .generalNewsTopic(true)
            .scholarshipNewsTopic(true)
            .eventNewsTopic(true)
            .academicNewsTopic(false)
            .employmentNewsTopic(false)
            .build();

        when(fcmTokenService.getFcmTokenBy(fcmToken)).thenReturn(fcmTokenDocument);
        when(topicConverter.toResponse(fcmTokenDocument)).thenReturn(topicStatusResponse);

        // When
        TopicStatusResponse result = topicBusiness.getTopicStatusBy(fcmToken);

        // Then
        assertThat(result.isGeneralNewsTopic()).isTrue();
        assertThat(result.isScholarshipNewsTopic()).isTrue();
        assertThat(result.isEventNewsTopic()).isTrue();
        assertThat(result.isAcademicNewsTopic()).isFalse();
        assertThat(result.isEmploymentNewsTopic()).isFalse();
    }

}
