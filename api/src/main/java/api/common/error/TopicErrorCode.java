package api.common.error;
import global.errorcode.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TopicErrorCode implements ErrorCodeIfs {
    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND.value(), 3000, "유효하지 않은 구독입니다.")
    ;
    private final Integer httpCode;
    private final Integer errorCode;
    private final String description;

}