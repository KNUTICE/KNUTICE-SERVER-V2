package api.domain.user.business;

import api.domain.jwt.business.JwtTokenBusiness;
import api.domain.jwt.model.JwtTokenResponse;
import api.domain.user.controller.model.duplication.DuplicationEmailRequest;
import api.domain.user.controller.model.login.UserLoginRequest;
import api.domain.user.controller.model.register.UserRegisterRequest;
import api.domain.user.converter.UserConverter;
import api.domain.user.service.UserService;
import db.domain.user.UserDocument;
import global.annotation.Business;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class UserBusiness {

    private final UserService userService;
    private final UserConverter userConverter;
    private final JwtTokenBusiness jwtTokenBusiness;

    public Boolean register(UserRegisterRequest userRegisterRequest) {

        userService.existsByEmailWithThrow(userRegisterRequest.getEmail());

        // 사용자 정보 저장
        UserDocument userDocument = userConverter.toDocument(userRegisterRequest);
        return  userService.register(userDocument);
    }

    public Boolean duplicationEmailCheck(DuplicationEmailRequest duplicationEmailRequest) {
        return userService.existsByEmailWithThrow(duplicationEmailRequest.getEmail());
    }

    public JwtTokenResponse login(UserLoginRequest userLoginRequest) {
        UserDocument userDocument = userService.login(userLoginRequest);
        return jwtTokenBusiness.issueToken(userDocument);
    }

}
