package api.domain.fcm.controller;

import api.common.feign.CrawlerClient;
import api.common.feign.request.FcmRequest;
import api.domain.fcm.controller.model.FcmTokenInfo;
import api.domain.fcm.business.FcmTokenBusiness;
import global.api.Api;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmApiController {

    private final FcmTokenBusiness fcmTokenBusiness;
    private final CrawlerClient crawlerClient;

    @GetMapping("/tokens")
    public Api<List<FcmTokenInfo>> getFcmTokenList() {
        List<FcmTokenInfo> responseList = fcmTokenBusiness.getFcmTokenList();
        return Api.OK(responseList);
    }

    @PostMapping("/messages")
    public Api<Boolean> sendMessage(@RequestBody @Valid Api<FcmRequest> request) {
        crawlerClient.sendMessage(request.getBody().getFcmToken(), request.getBody());
        return Api.OK(true);
    }

}
