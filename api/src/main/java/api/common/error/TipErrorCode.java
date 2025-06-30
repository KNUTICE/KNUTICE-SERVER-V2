package api.common.error;

import global.errorcode.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TipErrorCode implements ErrorCodeIfs {

    TIP_NOT_FOUND(HttpStatus.NOT_FOUND.value(), 5400, "TIP 정보가 존재하지 않습니다."),
    ;

    private final Integer httpCode;
    private final Integer errorCode;
    private final String description;

}
