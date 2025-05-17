package api.common.exception;
import api.common.error.UrgentNoticeErrorCode;
import api.common.exception.urgent.UrgentNoticeNotFoundException;
import global.api.Api;
import global.errorcode.ErrorCodeIfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UrgentNoticeExceptionHandler {

    @ExceptionHandler(value = UrgentNoticeNotFoundException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> notFoundUrgentNotice(UrgentNoticeNotFoundException e) {
//        log.error("", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Api.ERROR(UrgentNoticeErrorCode.URGENT_NOTICE_NOT_FOUND));
    }

}