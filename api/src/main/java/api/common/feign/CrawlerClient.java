package api.common.feign;

import api.common.feign.request.FcmRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "crawler-article", url = "${crawler.server.address}")
public interface CrawlerClient {

    @PostMapping("/message")
    void sendMessage(@RequestParam String fcmToken, @RequestBody FcmRequest fcmRequest);

    @PostMapping("/message/silent-push")
    void sendSilentPush(@RequestParam String fcmToken, @RequestBody FcmRequest fcmRequest);

    @PostMapping("/message/all-silent-push")
    void sendAllSilentPush(@RequestBody FcmRequest fcmRequest);


}
