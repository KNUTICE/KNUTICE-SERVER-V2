package api.domain.fcm.converter;

import api.domain.fcm.controller.model.FcmTokenRequest;
import db.domain.token.fcm.FcmTokenDocument;
import global.annotation.Converter;
import java.time.LocalDateTime;

@Converter
public class FcmTokenConverter {

    public FcmTokenDocument toEntity(FcmTokenRequest fcmTokenRequest) {
        return FcmTokenDocument.builder()
            .fcmToken(fcmTokenRequest.getFcmToken())
            .registeredAt(LocalDateTime.now())
            .build();
    }
}
