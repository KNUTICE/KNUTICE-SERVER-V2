package api.common.error;

import global.errorcode.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NoticeErrorCode implements ErrorCodeIfs {

    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), 1000, "게시글이 존재하지 않습니다."),
    NOTICE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), 1002, "게시글이 이미 존재합니다.")
    ;

    private final Integer httpCode;
    private final Integer errorCode;
    private final String description;

}
