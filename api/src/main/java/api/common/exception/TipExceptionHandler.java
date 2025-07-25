package api.common.exception;

import api.common.error.TipErrorCode;
import api.common.exception.tip.TipNotFoundException;
import global.api.Api;
import global.errorcode.ErrorCodeIfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TipExceptionHandler {

    @ExceptionHandler(value = TipNotFoundException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> NotFoundReportException(TipNotFoundException e) {
//        log.error("", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Api.ERROR(TipErrorCode.TIP_NOT_FOUND));
    }

}
