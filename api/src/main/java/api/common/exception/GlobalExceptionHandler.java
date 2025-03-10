package api.common.exception;
import global.api.Api;
import global.errorcode.ErrorCode;
import global.errorcode.ErrorCodeIfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * throw new BaseException("데이터베이스 연결 중 오류 발생", e);<br>
 * BaseException occurred with underlying cause: java.sql.SQLException: Connection refused<br>
 *
 * throw new BaseException("데이터베이스 연결 중 오류 발생", e); <br>
 * BaseException occurred with underlying cause: java.sql.SQLException: Connection refused<br>
 *
 * <br>
 * 두 로그 메세지의 차이는 원인 예외에 대한 로깅이 포함되냐 아니냐에 대한 차이.<br>
 * 예를들어, SQLException 발생하면, 해당 예외를 BaseException 을 상속 받은 예외가 처리한다면 구체적인 원인 예외를 알 수 없음.<br>
 * 만약 원인예외를 포함시킨다면 BaseException 으로 처리하되, 예외의 원인이되는 SQLException 을 표시함.<br>
 *
 * Author itstime0809
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 전역 예외 핸들러, Controller 및 Interceptor 에서 발생하는 모든 예외에 대한 처리기. <br>
     * 단, 예외정의는 각 CustomException 을 정의하고 BaseException 을 상속 받는다는 전제가 필요. <br>
     * Author itstime0809
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> handleBaseException(BaseException ex) {

        // 원인 예외가 존재할 경우: 원인 예외 정보와 함께 전체 스택 트레이스를 로깅
        if (ex.getCause() != null) {
            log.error("BaseException occurred with underlying cause: {}", ex.getCause(), ex);
        } else {
            // 원인 예외가 없을 경우: 예외 메시지와 함께 전체 스택 트레이스를 로깅
            log.error("BaseException occurred without cause: {}", ex.getMessage(), ex);
        }

        // ErrorCodeIfs 정의되어 있는 httpCode 사용.
        int status = ex.getErrorCodeIfs().getHttpCode();
        Api<ErrorCodeIfs> apiResponse = Api.ERROR(ex.getErrorCodeIfs(), ex.getDescription());
        return ResponseEntity.status(status).body(apiResponse);
    }


    /**
     * @Valid 검증에서 만약 검증에 실패하게 된다면 MethodArgumentNotValidException 발생.<br>
     * Author itstime0809
     */

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> methodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder builder = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }

        log.warn("", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Api.ERROR(ErrorCode.INVALID_INPUT_DATA, builder.toString()));
    }

}
