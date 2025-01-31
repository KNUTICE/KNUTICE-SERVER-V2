package api.domain.fcm.service;

import db.domain.token.fcm.FcmTokenDocument;
import db.domain.token.fcm.FcmTokenMongoRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenMongoRepository fcmTokenMongoRepository;

    public Optional<FcmTokenDocument> getFcmToken(String fcmToken) {
        return fcmTokenMongoRepository.findById(fcmToken);
    }

    public Boolean saveFcmToken(FcmTokenDocument fcmTokenDocument) {
        FcmTokenDocument savedFcmToken = fcmTokenMongoRepository.save(fcmTokenDocument);
        return savedFcmToken.getFcmToken() != null;
    }

    public FcmTokenDocument getFcmTokenBy(String fcmToken) {
        // TODO 예외처리
        return fcmTokenMongoRepository.findById(fcmToken)
            .orElseThrow(() -> new RuntimeException(""));

    }
}
