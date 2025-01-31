package api.common.exception;

import api.common.error.NoticeErrorCode;
import api.common.exception.notice.NoticeNotFoundException;
import global.api.Api;
import global.errorcode.ErrorCodeIfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class NoticeExceptionHandler {

    @ExceptionHandler(value = NoticeNotFoundException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> noticeNotFoundException(NoticeNotFoundException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Api.ERROR(NoticeErrorCode.NOTICE_NOT_FOUND));
    }

}
