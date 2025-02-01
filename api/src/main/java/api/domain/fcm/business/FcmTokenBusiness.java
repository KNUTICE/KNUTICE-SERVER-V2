package api.domain.fcm.business;

import api.domain.fcm.controller.model.FcmTokenRequest;
import api.domain.fcm.converter.FcmTokenConverter;
import api.domain.fcm.service.FcmService;
import db.domain.token.fcm.FcmTokenDocument;
import global.annotation.Business;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class FcmTokenBusiness {

    private final FcmService fcmService;

    private final FcmTokenConverter fcmTokenConverter;

    public Boolean saveFcmToken(FcmTokenRequest fcmTokenRequest) {

        Optional<FcmTokenDocument> fcmTokenDocument = fcmService.getFcmToken(
            fcmTokenRequest.getFcmToken());

        /**
         * 기존 토큰이 존재한다면 RegisteredAt, failed_token 변경 후 토큰 저장
         * 기존 토큰이 존재하지 않다면 새 토큰 저장함
         */
        if(fcmTokenDocument.isPresent()) {
            FcmTokenDocument existsFcmTokenDocument = fcmTokenDocument.get();
            existsFcmTokenDocument.setRegisteredAt(LocalDateTime.now());
            existsFcmTokenDocument.setFailedCount(0);
            return fcmService.saveFcmToken(existsFcmTokenDocument);
        }else { // @Builder.Default 를 지정했기 때문에, set() 할 필요 없음
            FcmTokenDocument newFcmTokenDocument = fcmTokenConverter.toDocument(fcmTokenRequest);
            return fcmService.saveFcmToken(newFcmTokenDocument);
        }
    }

}

