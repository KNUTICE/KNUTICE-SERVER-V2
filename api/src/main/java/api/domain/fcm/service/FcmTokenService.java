package api.domain.fcm.service;

import api.common.error.FcmTokenErrorCode;
import api.common.exception.fcm.FcmTokenNotFoundException;
import db.domain.token.fcm.FcmTokenDocument;
import db.domain.token.fcm.FcmTokenMongoRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenMongoRepository fcmTokenMongoRepository;

    public Optional<FcmTokenDocument> getFcmToken(String fcmToken) {
        return fcmTokenMongoRepository.findById(fcmToken);
    }

    public Boolean saveFcmToken(FcmTokenDocument fcmTokenDocument) {
        FcmTokenDocument savedFcmToken = fcmTokenMongoRepository.save(fcmTokenDocument);
        return savedFcmToken.getFcmToken() != null;
    }

    public FcmTokenDocument getFcmTokenBy(String fcmToken) {
        return fcmTokenMongoRepository.findById(fcmToken)
            .orElseThrow(() -> new FcmTokenNotFoundException(FcmTokenErrorCode.TOKEN_NOT_FOUND));

    }

    public boolean existsBy(String fcmToken) {
        return fcmTokenMongoRepository.existsById(fcmToken);
    }

}
