package api.domain.fcm.controller;

import api.domain.fcm.business.FcmTokenBusiness;
import api.domain.fcm.controller.model.FcmTokenRequest;
import api.domain.fcm.controller.model.FcmTokenUpdateRequest;
import global.api.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @PatchMapping("/open-api/fcm/tokens")
    public Api<Boolean> updateFcmToken(
        @RequestBody @Valid Api<FcmTokenUpdateRequest> fcmTokenUpdateRequest
    ) {
        log.info("token update - [old : {}] - [new : {}]",
            fcmTokenUpdateRequest.getBody().getOldFcmToken(),
            fcmTokenUpdateRequest.getBody().getNewFcmToken());
        return Api.OK(fcmTokenBusiness.updateFcmToken(fcmTokenUpdateRequest.getBody()));
    }

}
