package api.domain.user.controller;

import api.domain.jwt.model.JwtTokenResponse;
import api.domain.user.business.UserBusiness;
import api.domain.user.controller.model.duplication.DuplicationEmailRequest;
import api.domain.user.controller.model.login.UserLoginRequest;
import api.domain.user.controller.model.register.UserRegisterRequest;
import global.api.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api/users")
public class UserOpenApiController {

    private final UserBusiness userBusiness;

    @PostMapping()
    public Api<Boolean> register(
        @RequestBody @Valid Api<UserRegisterRequest> userRegisterRequest
    ) {
        return Api.OK(userBusiness.register(userRegisterRequest.getBody()));
    }

    @GetMapping("/check-email")
    public Api<Boolean> duplicationEmailCheck(
        @ModelAttribute @Valid DuplicationEmailRequest duplicationEmailRequest
    ) {
        return Api.OK(userBusiness.duplicationEmailCheck(duplicationEmailRequest));
    }

    @PostMapping("/login")
    public Api<JwtTokenResponse> login(@RequestBody @Valid Api<UserLoginRequest> userLoginRequest) {
        return Api.OK(userBusiness.login(userLoginRequest.getBody()));
    }

}
