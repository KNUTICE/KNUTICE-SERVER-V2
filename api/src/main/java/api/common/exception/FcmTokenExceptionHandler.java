package api.common.exception;

import api.common.error.FcmTokenErrorCode;
import api.common.exception.fcm.FcmTokenNotFoundException;
import global.api.Api;
import global.errorcode.ErrorCodeIfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FcmTokenExceptionHandler {

    @ExceptionHandler(value = FcmTokenNotFoundException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> tokenNotFoundException(FcmTokenNotFoundException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Api.ERROR(FcmTokenErrorCode.TOKEN_NOT_FOUND));
    }

}
