package api.domain.fcm.controller;

import api.domain.fcm.business.FcmTokenBusiness;
import api.domain.fcm.controller.model.FcmTokenRequest;
import global.api.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api/fcm")
public class FcmTokenOpenApiController {

    private final FcmTokenBusiness fcmTokenBusiness;

    @PostMapping({"", "/tokens"})
    public Api<Boolean> saveFcmToken(
        @RequestBody @Valid Api<FcmTokenRequest> fcmTokenRequest
    ) {
        Boolean response = fcmTokenBusiness.saveFcmToken(fcmTokenRequest.getBody());
        return Api.OK(response);
    }

}
