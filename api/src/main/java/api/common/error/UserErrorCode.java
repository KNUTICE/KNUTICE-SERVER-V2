package api.common.error;

import global.errorcode.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCodeIfs {

    USER_EXISTS(409, 1150, "이미 존재하는 계정입니다."),
    EMAIL_EXISTS(409, 1151, "이미 사용 중인 이메일입니다."),
    NICKNAME_EXISTS(409, 1152, "이미 사용 중인 닉네임입니다."),
    USER_NOT_FOUND(404, 1153, "사용자를 찾을 수 없습니다."),
    PASSWORD_MISMATCH(401, 1154, "비밀번호가 일치하지 않습니다.")
    ;


    private final Integer httpCode;

    private final Integer errorCode;

    private final String description;

}
