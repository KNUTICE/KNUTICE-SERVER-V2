package api.domain.topic.converter;

import api.domain.topic.controller.model.TopicStatusResponse;
import db.domain.token.fcm.FcmTokenDocument;
import global.annotation.Converter;

@Converter
public class TopicConverter {

    public TopicStatusResponse toResponse(FcmTokenDocument fcmTokenDocument) {
        return TopicStatusResponse.builder()
            .generalNewsTopic(fcmTokenDocument.isGeneralNewsTopic())
            .scholarshipNewsTopic(fcmTokenDocument.isScholarshipNewsTopic())
            .eventNewsTopic(fcmTokenDocument.isEventNewsTopic())
            .academicNewsTopic(fcmTokenDocument.isAcademicNewsTopic())
            .employmentNewsTopic(fcmTokenDocument.isEmploymentNewsTopic())
            .build();
    }

}
