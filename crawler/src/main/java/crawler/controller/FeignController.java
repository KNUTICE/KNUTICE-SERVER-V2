package crawler.controller;

import crawler.service.FcmService;
import crawler.service.model.FcmDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeignController {

    private final FcmService fcmService;

    @PostMapping("/message")
    public void sendMessage(@RequestParam String fcmToken, @RequestBody FcmDto fcmDto) {
        fcmService.batchSend(fcmDto, List.of(fcmToken), false);
    }

    @PostMapping("/message/silent-push")
    public void sendSilentPush(@RequestParam String fcmToken) {
        // dummy 값 표출
        fcmService.batchSend(new FcmDto(), List.of(fcmToken), true);
    }

}
