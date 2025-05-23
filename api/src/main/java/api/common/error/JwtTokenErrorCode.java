package api.common.error;

import global.errorcode.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtTokenErrorCode implements ErrorCodeIfs {

    INVALID_TOKEN(401,5100,"유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401,5101,"만료된 토큰입니다."),
    TOKEN_EXCEPTION(401,5102,"알 수 없는 토큰 에러입니다."),
    ;

    private final Integer httpCode;
    private final Integer errorCode;
    private final String description;

}
