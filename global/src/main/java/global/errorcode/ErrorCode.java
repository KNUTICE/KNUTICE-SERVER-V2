package global.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeIfs{

    OK(HttpStatus.OK.value(),200,"성공"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(),400 , "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),500,"서버 내부에 에러가 발생했습니다."),
    NULL_POINT(HttpStatus.INTERNAL_SERVER_ERROR.value(), 512,"NULL POINT 입니다."),
    INVALID_INPUT_DATA(HttpStatus.BAD_REQUEST.value(), 400,"유효하지 않은 입력값입니다."),
    ;

    private final Integer httpCode;
    private final Integer errorCode;
    private final String description;

}
