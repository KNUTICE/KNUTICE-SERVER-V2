package api.domain.user.service;

import api.common.error.UserErrorCode;
import api.common.exception.user.UserNotFoundException;
import api.common.exception.user.EmailExistsException;
import api.common.exception.user.PasswordMismatchException;
import api.domain.user.controller.model.login.UserLoginRequest;
import db.domain.user.UserDocument;
import db.domain.user.UserMongoRepository;
import db.domain.user.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMongoRepository userMongoRepository;

    public Boolean save(UserDocument userDocument) {
        return userMongoRepository.save(userDocument).getId() != null;
    }

    public Boolean existsByEmail(String email) {
        return userMongoRepository.existsByAccount_Email(email);
    }

    public Optional<UserDocument> getUser(String email) {
        return userMongoRepository.findFirstByAccount_Email(email);
    }

}
