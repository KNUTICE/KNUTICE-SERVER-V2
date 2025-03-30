package api.domain.fcm.converter;

import api.domain.admin.controller.model.response.FcmTokenInfoList;
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
            .apnsEnabled(fcmTokenRequest.getApnsEnabled())
            .registeredAt(LocalDateTime.now())
            .build();
    }

    public FcmTokenInfoList toResponse(FcmTokenDocument fcmTokenDocument) {
        return FcmTokenInfoList.builder()
            .fcmToken(fcmTokenDocument.getFcmToken())
            .generalNewsTopic(fcmTokenDocument.isGeneralNewsTopic())
            .scholarshipNewsTopic(fcmTokenDocument.isScholarshipNewsTopic())
            .eventNewsTopic(fcmTokenDocument.isEventNewsTopic())
            .academicNewsTopic(fcmTokenDocument.isAcademicNewsTopic())
            .registeredAt(fcmTokenDocument.getRegisteredAt())
            .failedCount(fcmTokenDocument.getFailedCount())
            .build();
    }

    public List<FcmTokenInfoList> toListResponse(List<FcmTokenDocument> fcmTokenDocumentList) {
        return fcmTokenDocumentList.stream()
            .map(this::toResponse)
            .toList();
    }

}
