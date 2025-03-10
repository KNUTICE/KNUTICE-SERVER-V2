package api.common.exception;

import global.errorcode.ErrorCodeIfs;
import lombok.Getter;

/**
 * super() 메소드를 통해, 하위 구체클래스에서 발생한 예외를 전달 받음. <br>
 *
 * 각 생성자는 상황과 필요에 맞게 사용하며, 각 생성자의 역할은 다음과 같습니다.
 *
 * <br>
 * 1. ErrorCodeIfs & Description<br>
 *    에러코드와, 본 예외에 대한 설명만 전달합니다. 예외체이닝을 적용할 필요가 없다면 본 생성자를 사용할 수 있습니다. <br>
 * <br>
 * 2. ErrorCodeIfs & Throwable
 *    예외체이닝이 필요한 경우 즉 예외의 원인을 보존해야하고, 그 예외에 대한 설명은 필요하지 않은 경우 본 생성자를 사용할 수 있습니다. <br>
 * <br>
 * 3. ErrorCodeIfs & Throwable & Description
 *    예외체이닝과 예외에 대한 설명, 에러코드 모두 필요한 경우, 본 생성자를 사용할 수 있습니다. <br>
 * <br>
 *
 * BaseException 의 처리는 GlobalExceptionHandler 에서 공통으로 처리합니다.
 * <br>
 *
 * Author itstime0809
 */


@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorCodeIfs errorCodeIfs;
    private String description;

    public BaseException(ErrorCodeIfs errorCodeIfs) {
        this.errorCodeIfs = errorCodeIfs;
    }

    public BaseException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
        super(throwable);
        this.errorCodeIfs = errorCodeIfs;
    }

    public BaseException(ErrorCodeIfs errorCodeIfs, String description) {
        this.errorCodeIfs = errorCodeIfs;
        this.description = description;
    }

    public BaseException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
        super(description, throwable);
        this.errorCodeIfs = errorCodeIfs;
        this.description = description;
    }
}
