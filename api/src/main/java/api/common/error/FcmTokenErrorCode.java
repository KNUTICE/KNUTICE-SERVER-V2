package api.common.error;

import global.errorcode.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum FcmTokenErrorCode implements ErrorCodeIfs {


    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), 2000, "토큰이 존재하지 않습니다.");

    private final Integer httpCode;
    private final Integer errorCode;
    private final String description;

}
