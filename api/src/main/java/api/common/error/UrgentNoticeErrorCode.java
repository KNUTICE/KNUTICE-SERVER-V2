package api.common.error;

import global.errorcode.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UrgentNoticeErrorCode implements ErrorCodeIfs {
    URGENT_NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), 4000, "긴급 공지 게시글이 존재하지 않습니다.")
    ;
    private final Integer httpCode;
    private final Integer errorCode;
    private final String description;

}
