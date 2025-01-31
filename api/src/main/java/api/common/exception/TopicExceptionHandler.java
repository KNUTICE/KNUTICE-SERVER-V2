package api.common.exception;
import api.common.error.TopicErrorCode;
import api.common.exception.topic.TopicNotFoundException;
import global.api.Api;
import global.errorcode.ErrorCodeIfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class TopicExceptionHandler {

    @ExceptionHandler(value = TopicNotFoundException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> tokenNotFoundException(TopicNotFoundException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Api.ERROR(TopicErrorCode.TOPIC_NOT_FOUND));
    }

}