package api.domain.user.service;

import api.common.error.UserErrorCode;
import api.common.exception.token.UserNotFoundException;
import api.common.exception.user.EmailExistsException;
import api.common.exception.user.PasswordMismatchException;
import api.domain.user.controller.model.login.UserLoginRequest;
import db.domain.user.UserDocument;
import db.domain.user.UserMongoRepository;
import db.domain.user.enums.UserStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMongoRepository userMongoRepository;

    public Boolean register(UserDocument userDocument) {
        userDocument.setStatus(UserStatus.PENDING);
        userDocument.setRegisteredAt(LocalDateTime.now());
        UserDocument savedUserDocument = userMongoRepository.save(userDocument);
        return savedUserDocument.getId() != null;
    }

    public Boolean existsByEmailWithThrow(String email) {
        boolean existsByEmail = userMongoRepository.existsByAccount_Email(email);
        if (existsByEmail) {
            throw new EmailExistsException(UserErrorCode.EMAIL_EXISTS);
        }
        return true;
    }

    public UserDocument login(UserLoginRequest userLoginRequest) {

        UserDocument userDocument = userMongoRepository.findFirstByAccount_EmailAndStatus(
                userLoginRequest.getEmail(), UserStatus.REGISTERED)
            .orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND));

        if (BCrypt.checkpw(userLoginRequest.getPassword(), userDocument.getAccount().getPassword())) {
            userDocument.setLastLoginAt(LocalDateTime.now());
            userMongoRepository.save(userDocument);
            return userDocument;
        }

        throw new PasswordMismatchException(UserErrorCode.PASSWORD_MISMATCH);
    }

    public UserDocument getUserWithThrow(String userId) {
        return userMongoRepository.findFirstByIdAndStatus(userId, UserStatus.REGISTERED)
            .orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND));
    }

}
