package api.domain.token.business;

import api.domain.token.controller.model.DeviceTokenRequest;
import api.domain.token.converter.TokenConverter;
import api.domain.token.service.TokenService;
import db.domain.token.DeviceTokenDocument;
import global.annotation.Business;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class TokenBusiness {

    private final TokenService tokenService;

    private final TokenConverter tokenConverter;

    public Boolean saveDeviceToken(DeviceTokenRequest deviceTokenRequest) {

        Optional<DeviceTokenDocument> deviceTokenDocument = tokenService.getDeviceToken(
            deviceTokenRequest.getDeviceToken());

        /**
         * 기존 토큰이 존재한다면 RegisteredAt, failed_token 변경 후 토큰 저장
         * 기존 토큰이 존재하지 않다면 새 토큰 저장함
         */
        if(deviceTokenDocument.isPresent()) {
            DeviceTokenDocument existsDeviceTokenDocument = deviceTokenDocument.get();
            existsDeviceTokenDocument.setRegisteredAt(LocalDateTime.now());
            existsDeviceTokenDocument.setFailedCount(0);
            return tokenService.saveDeviceToken(existsDeviceTokenDocument);
        }else { // @Builder.Default 를 지정했기 때문에, set() 할 필요 없음
            DeviceTokenDocument newDeviceTokenDocument = tokenConverter.toEntity(deviceTokenRequest);
            return tokenService.saveDeviceToken(newDeviceTokenDocument);
        }
    }

}

