package api.domain.fcm.service;

import api.common.error.FcmTokenErrorCode;
import api.common.exception.fcm.FcmTokenNotFoundException;
import api.infra.secon.FcmTokenMongoSeconRepository;
import api.infra.secon.FcmTokenSeconDocument;
import db.domain.token.fcm.FcmTokenDocument;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenSeconService {

    private final FcmTokenMongoSeconRepository fcmTokenMongoSeconRepository;

    public Optional<FcmTokenSeconDocument> getFcmToken(String fcmToken) {
        return fcmTokenMongoSeconRepository.findById(fcmToken);
    }

    public FcmTokenSeconDocument getFcmTokenBy(String fcmToken) {
        return fcmTokenMongoSeconRepository.findById(fcmToken)
            .orElseThrow(() -> new FcmTokenNotFoundException(FcmTokenErrorCode.TOKEN_NOT_FOUND));
    }

        public Boolean saveFcmToken(FcmTokenSeconDocument fcmTokenSeconDocument) {
        FcmTokenSeconDocument savedFcmToken = fcmTokenMongoSeconRepository.save(fcmTokenSeconDocument);
        return savedFcmToken.getFcmToken() != null;
    }

    public boolean deleteBy(String fcmToken) {
        fcmTokenMongoSeconRepository.deleteById(fcmToken);
        return true;
    }

    public boolean existsBy(String fcmToken) {
        return fcmTokenMongoSeconRepository.existsById(fcmToken);
    }


}
