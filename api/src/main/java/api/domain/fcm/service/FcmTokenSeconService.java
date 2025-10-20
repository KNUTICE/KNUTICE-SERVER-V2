package api.domain.fcm.service;

import api.infra.secon.FcmTokenMongoSeconRepository;
import api.infra.secon.FcmTokenSeconDocument;
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

    public Boolean saveFcmToken(FcmTokenSeconDocument fcmTokenSeconDocument) {
        FcmTokenSeconDocument savedFcmToken = fcmTokenMongoSeconRepository.save(fcmTokenSeconDocument);
        return savedFcmToken.getFcmToken() != null;
    }

    public boolean delete(String fcmToken) {
        fcmTokenMongoSeconRepository.deleteById(fcmToken);
        return true;
    }

}
