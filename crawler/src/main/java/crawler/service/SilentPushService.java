package crawler.service;

import crawler.service.model.FcmDto;
import db.domain.token.fcm.FcmTokenDocument;
import db.domain.token.fcm.FcmTokenMongoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SilentPushService {

    private final FcmTokenMongoRepository fcmTokenMongoRepository;
    private final FcmService fcmService;

    public void sendSilentPush(String fcmToken, FcmDto fcmDto) {
        fcmService.batchSend(fcmDto, List.of(fcmToken), true);
    }

    public void sendAllSilentPush(FcmDto fcmDto) {
        List<FcmTokenDocument> fcmTokenDocuments = fcmTokenMongoRepository.findAll();
        var fcmTokens = fcmTokenDocuments.stream().map(FcmTokenDocument::getFcmToken).toList();
        fcmService.batchSend(fcmDto, fcmTokens, true);
    }


}
