package api.domain.fcm.converter;

import api.domain.fcm.controller.model.FcmTokenInfo;
import api.domain.fcm.controller.model.FcmTokenRequest;
import db.domain.token.fcm.FcmTokenDocument;
import global.annotation.Converter;
import java.time.LocalDateTime;
import java.util.List;

@Converter
public class FcmTokenConverter {

    public FcmTokenDocument toDocument(FcmTokenRequest fcmTokenRequest) {
        return FcmTokenDocument.builder()
            .fcmToken(fcmTokenRequest.getFcmToken())
            .registeredAt(LocalDateTime.now())
            .build();
    }

    public FcmTokenInfo toResponse(FcmTokenDocument fcmTokenDocument) {
        return FcmTokenInfo.builder()
            .fcmToken(fcmTokenDocument.getFcmToken())
            .generalNewsTopic(fcmTokenDocument.isGeneralNewsTopic())
            .scholarshipNewsTopic(fcmTokenDocument.isScholarshipNewsTopic())
            .eventNewsTopic(fcmTokenDocument.isEventNewsTopic())
            .academicNewsTopic(fcmTokenDocument.isAcademicNewsTopic())
            .employmentNewsTopic(fcmTokenDocument.isEmploymentNewsTopic())
            .registeredAt(fcmTokenDocument.getRegisteredAt())
            .failedCount(fcmTokenDocument.getFailedCount())
            .build();
    }

    public List<FcmTokenInfo> toListResponse(List<FcmTokenDocument> fcmTokenDocumentList) {
        return fcmTokenDocumentList.stream()
            .map(this::toResponse)
            .toList();
    }

}
