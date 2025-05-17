package crawler.controller;

import crawler.service.FcmService;
import crawler.service.model.FcmDto;
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

    @PostMapping("/message")
    public void sendMessage(@RequestParam String fcmToken, @RequestBody FcmDto fcmDto) {
        fcmService.batchSend(fcmDto, List.of(fcmToken));
    }

}
