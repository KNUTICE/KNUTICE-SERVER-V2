package api.domain.token.converter;

import api.domain.token.controller.model.DeviceTokenRequest;
import db.domain.token.DeviceTokenDocument;
import global.annotation.Converter;
import java.time.LocalDateTime;

@Converter
public class TokenConverter {

    public DeviceTokenDocument toEntity(DeviceTokenRequest deviceTokenRequest) {
        return DeviceTokenDocument.builder()
            .token(deviceTokenRequest.getDeviceToken())
            .registeredAt(LocalDateTime.now())
            .build();
    }
}
