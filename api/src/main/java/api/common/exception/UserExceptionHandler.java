package api.common.exception;

import api.common.error.UserErrorCode;
import api.common.exception.user.UserNotFoundException;
import api.common.exception.user.EmailExistsException;
import api.common.exception.user.PasswordMismatchException;
import api.common.exception.user.UserExistsException;
import api.common.exception.user.UserNotPermitted;
import global.api.Api;
import global.errorcode.ErrorCodeIfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(value = UserExistsException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> existsUserException(UserExistsException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Api.ERROR(UserErrorCode.USER_EXISTS));
    }

    @ExceptionHandler(value = EmailExistsException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> existsEmailException(EmailExistsException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Api.ERROR(UserErrorCode.EMAIL_EXISTS));
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> notFoundUserException(UserNotFoundException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Api.ERROR(UserErrorCode.USER_NOT_FOUND));
    }

    @ExceptionHandler(value = PasswordMismatchException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> mismatchPasswordException(PasswordMismatchException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Api.ERROR(UserErrorCode.PASSWORD_MISMATCH));
    }

    @ExceptionHandler(value = UserNotPermitted.class)
    public ResponseEntity<Api<ErrorCodeIfs>> notPermittedUser(UserNotPermitted e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Api.ERROR(UserErrorCode.USER_NOT_FOUND));
    }

}
