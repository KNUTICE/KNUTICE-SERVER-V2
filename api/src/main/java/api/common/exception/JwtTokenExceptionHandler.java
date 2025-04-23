package api.common.exception;

import api.common.error.JwtTokenErrorCode;
import api.common.exception.jwt.JwtTokenException;
import api.common.exception.jwt.JwtTokenExpiredException;
import api.common.exception.jwt.JwtTokenSignatureException;
import global.api.Api;
import global.errorcode.ErrorCodeIfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class JwtTokenExceptionHandler {

    @ExceptionHandler(value = JwtTokenException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> tokenException(JwtTokenException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Api.ERROR(JwtTokenErrorCode.TOKEN_EXCEPTION));
    }

    @ExceptionHandler(value = JwtTokenSignatureException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> tokenSignatureException(JwtTokenSignatureException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Api.ERROR(JwtTokenErrorCode.INVALID_TOKEN));
    }

    @ExceptionHandler(value = JwtTokenExpiredException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> tokenExpiredException(JwtTokenExpiredException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Api.ERROR(JwtTokenErrorCode.EXPIRED_TOKEN));
    }

}
