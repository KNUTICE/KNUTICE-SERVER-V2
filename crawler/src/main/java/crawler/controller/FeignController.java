package crawler.controller;

import crawler.fcmutils.dto.FcmTokenDetail;
import crawler.service.FcmService;
import crawler.service.model.FcmDto;
import db.domain.token.fcm.FcmTokenMongoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeignController {

    private final FcmService fcmService;
    private final FcmTokenMongoRepository fcmTokenMongoRepository;

    @PostMapping("/message")
    public void sendMessage(@RequestParam String fcmToken, @RequestBody FcmDto fcmDto) {
        fcmTokenMongoRepository.findById(fcmToken).ifPresent(fcmTokenDocument -> {

            // apnsEnabled 가 null 이면 false 처리, true 일 때만 badgeCount 증가
            boolean apnsEnabled = (fcmTokenDocument.getApnsEnabled() != null) ? fcmTokenDocument.getApnsEnabled() : false;
            int badgeCount = apnsEnabled ? fcmTokenDocument.getBadgeCount() + 1 : fcmTokenDocument.getBadgeCount();

            // FcmTokenDetail 생성 후 batchSend 호출
            fcmService.batchSend(fcmDto, List.of(
                new FcmTokenDetail(fcmToken, apnsEnabled, badgeCount)
            ));
        });
    }

}
