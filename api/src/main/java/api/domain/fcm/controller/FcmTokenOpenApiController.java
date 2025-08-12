package api.domain.fcm.controller;

import api.domain.fcm.business.FcmTokenBusiness;
import api.domain.fcm.controller.model.FcmTokenRequest;
import global.api.Api;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping()
public class FcmTokenOpenApiController {

    private final FcmTokenBusiness fcmTokenBusiness;

    @PostMapping({"/open-api/fcm", "/open-api/fcm/tokens"})
    public Api<Boolean> saveFcmToken(
        @RequestBody @Valid Api<FcmTokenRequest> fcmTokenRequest
    ) {
        Boolean response = fcmTokenBusiness.saveFcmToken(fcmTokenRequest.getBody());
        return Api.OK(response);
    }

    @PostMapping("/open-api/fcm/tokens/silent-push")
    public Api<Boolean> sendSilentPushFcmToken(
        @RequestBody @Valid Api<FcmTokenRequest> fcmTokenRequest
    ) {
        log.info("Silent Push Register Date : {}", LocalDateTime.now());
        log.info("silent-push : {}", fcmTokenRequest.getBody().getFcmToken());
        Boolean response = fcmTokenBusiness.saveFcmToken(fcmTokenRequest.getBody());
        return Api.OK(response);
    }

}
