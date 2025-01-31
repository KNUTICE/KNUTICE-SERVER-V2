package api.common.exception;

import api.common.error.JwtTokenErrorCode;
import api.common.exception.token.TokenException;
import api.common.exception.token.TokenExpiredException;
import api.common.exception.token.TokenSignatureException;
import global.api.Api;
import global.errorcode.ErrorCodeIfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TokenExceptionHandler {

    @ExceptionHandler(value = TokenException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> tokenException(TokenException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Api.ERROR(JwtTokenErrorCode.TOKEN_EXCEPTION));
    }

    @ExceptionHandler(value = TokenSignatureException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> tokenSignatureException(TokenSignatureException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Api.ERROR(JwtTokenErrorCode.INVALID_TOKEN));
    }

    @ExceptionHandler(value = TokenExpiredException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> tokenExpiredException(TokenExpiredException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Api.ERROR(JwtTokenErrorCode.EXPIRED_TOKEN));
    }

}
