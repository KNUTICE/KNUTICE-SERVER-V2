package api.domain.user.controller;

import api.domain.jwt.model.JwtTokenResponse;
import api.domain.user.business.UserBusiness;
import api.domain.user.controller.model.duplication.DuplicationEmailRequest;
import api.domain.user.controller.model.login.UserLoginRequest;
import api.domain.user.controller.model.register.UserRegisterRequest;
import global.api.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api/user")
public class UserOpenApiController {

    private final UserBusiness userBusiness;

    @PostMapping()
    public Api<Boolean> register(
        @RequestBody @Valid Api<UserRegisterRequest> userRegisterRequest
    ) {
        Boolean response = userBusiness.register(userRegisterRequest.getBody());
        return Api.OK(response);
    }

    @PostMapping("/duplication/email")
    public Api<Boolean> duplicationEmailCheck(
        @RequestBody @Valid Api<DuplicationEmailRequest> duplicationEmailRequest
    ) {
        Boolean response = userBusiness.duplicationEmailCheck(duplicationEmailRequest.getBody());
        return Api.OK(response);
    }

    @PostMapping("/login")
    public Api<JwtTokenResponse> login(@RequestBody @Valid Api<UserLoginRequest> userLoginRequest) {
        JwtTokenResponse response = userBusiness.login(userLoginRequest.getBody());
        return Api.OK(response);
    }

}
