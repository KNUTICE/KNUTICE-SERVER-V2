package api.domain.token.controller;

import api.domain.token.business.TokenBusiness;
import api.domain.token.controller.model.DeviceTokenRequest;
import global.api.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api/token")
public class TokenOpenApiController {

    private final TokenBusiness tokenBusiness;

    @PostMapping
    public Api<Boolean> saveDeviceToken(
        @RequestBody @Valid Api<DeviceTokenRequest> deviceTokenRequest
    ) {
        Boolean response = tokenBusiness.saveDeviceToken(deviceTokenRequest.getBody());
        return Api.OK(response);
    }

}
