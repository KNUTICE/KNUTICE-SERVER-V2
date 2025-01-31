package api.domain.user.converter;

import api.domain.user.controller.model.register.UserRegisterRequest;
import db.domain.user.Account;
import db.domain.user.UserDocument;
import db.domain.user.enums.UserRole;
import global.annotation.Converter;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

@Converter
@RequiredArgsConstructor
public class UserConverter {

    public UserDocument toDocument(UserRegisterRequest userRegisterRequest) {
        return UserDocument.builder()
            .account(
                Account.builder()
                    .email(userRegisterRequest.getEmail())
                    .password(BCrypt.hashpw(userRegisterRequest.getPassword(), BCrypt.gensalt()))
                    .build()
            )
            .name(userRegisterRequest.getName())
            .role(UserRole.BASIC_USER)
            .build();
    }

}
