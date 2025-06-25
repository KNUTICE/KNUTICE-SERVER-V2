package api.common.exception;

import api.common.error.ImageErrorCode;
import api.common.error.UserErrorCode;
import api.common.exception.image.ImageNotFoundException;
import api.common.exception.image.ImageStorageDirectoryCreationException;
import api.common.exception.image.ImageStorageWriteException;
import api.common.exception.image.InvalidImageExtensionException;
import api.common.exception.user.EmailExistsException;
import api.common.exception.user.PasswordMismatchException;
import api.common.exception.user.UserExistsException;
import api.common.exception.user.UserNotFoundException;
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
public class ImageExceptionHandler {

    @ExceptionHandler(value = ImageNotFoundException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> handleImageNotFoundException(
        ImageNotFoundException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Api.ERROR(ImageErrorCode.IMAGE_NOT_FOUND));
    }

    @ExceptionHandler(value = InvalidImageExtensionException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> handleInvalidImageExtensionException(
        InvalidImageExtensionException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Api.ERROR(ImageErrorCode.INVALID_EXTENSION));
    }

    @ExceptionHandler(value = ImageStorageDirectoryCreationException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> handleImageStorageDirectoryCreationException(
        ImageStorageDirectoryCreationException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Api.ERROR(ImageErrorCode.DIRECTORY_CREATION_FAILED));
    }

    @ExceptionHandler(value = ImageStorageWriteException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> handleImageStorageWriteException(
        ImageStorageWriteException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Api.ERROR(ImageErrorCode.IMAGE_STORAGE_FAILED));
    }

}
