package api.domain.user.business;

import api.common.error.UserErrorCode;
import api.common.exception.user.EmailExistsException;
import api.common.exception.user.PasswordMismatchException;
import api.common.exception.user.UserNotFoundException;
import api.common.exception.user.UserNotPermitted;
import api.domain.jwt.business.JwtTokenBusiness;
import api.domain.jwt.model.JwtTokenResponse;
import api.domain.user.controller.model.duplication.DuplicationEmailRequest;
import api.domain.user.controller.model.login.UserLoginRequest;
import api.domain.user.controller.model.register.UserRegisterRequest;
import api.domain.user.converter.UserConverter;
import api.domain.user.service.UserService;
import db.domain.user.UserDocument;
import db.domain.user.enums.UserStatus;
import global.annotation.Business;
import java.time.LocalDateTime;
import javax.naming.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

@Business
@RequiredArgsConstructor
public class UserBusiness {

    private final UserService userService;
    private final UserConverter userConverter;
    private final JwtTokenBusiness jwtTokenBusiness;

    public Boolean register(UserRegisterRequest userRegisterRequest) {
        existsByEmail(userRegisterRequest.getEmail());

        // 사용자 정보 저장
        UserDocument userDocument = userConverter.toDocument(userRegisterRequest);
        userDocument.setStatus(UserStatus.PENDING); // 승인 대기
        userDocument.setRegisteredAt(LocalDateTime.now());
        return userService.save(userDocument);
    }

    public Boolean duplicationEmailCheck(DuplicationEmailRequest duplicationEmailRequest) {
        existsByEmail(duplicationEmailRequest.getEmail());
        return true;
    }

    public JwtTokenResponse login(UserLoginRequest userLoginRequest) {

        UserDocument userDocument = userService.getUser(userLoginRequest.getEmail())
            .orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND));

        if (userDocument.getStatus().equals(UserStatus.PENDING)) {
            throw new UserNotPermitted(UserErrorCode.USER_NOT_PERMITTED);
        }

        if (!BCrypt.checkpw(userLoginRequest.getPassword(), userDocument.getAccount().getPassword())) {
            throw new PasswordMismatchException(UserErrorCode.PASSWORD_MISMATCH);
        }

        // 마지막 로그인 시간 저장
        userDocument.setLastLoginAt(LocalDateTime.now());
        userService.save(userDocument);

        return jwtTokenBusiness.issueToken(userDocument);
    }

    private void existsByEmail(String email) {
        if (userService.existsByEmail(email)) {
            throw new EmailExistsException(UserErrorCode.EMAIL_EXISTS);
        }
    }

}
