package api.common.exception.user;

import global.errorcode.ErrorCodeIfs;
import lombok.Getter;

@Getter
public class UserExistsException extends RuntimeException {

    private final ErrorCodeIfs errorCodeIfs;
    private final String description;

    public UserExistsException(ErrorCodeIfs errorCodeIfs) {
        super(errorCodeIfs.getDescription());
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorCodeIfs.getDescription();
    }

    public UserExistsException(ErrorCodeIfs errorCodeIfs, String errorDescription) {
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorDescription;
    }

    public UserExistsException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
        super(throwable);
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorCodeIfs.getDescription();
    }

    public UserExistsException(ErrorCodeIfs errorCodeIfs, Throwable throwable,
        String errorDescription) {
        super(throwable);
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorDescription;
    }

}
