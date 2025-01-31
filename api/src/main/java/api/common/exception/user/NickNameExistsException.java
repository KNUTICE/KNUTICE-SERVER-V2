package api.common.exception.user;

import global.errorcode.ErrorCodeIfs;
import lombok.Getter;

@Getter
public class NickNameExistsException extends RuntimeException {

    private final ErrorCodeIfs errorCodeIfs;
    private final String description;

    public NickNameExistsException(ErrorCodeIfs errorCodeIfs) {
        super(errorCodeIfs.getDescription());
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorCodeIfs.getDescription();
    }

    public NickNameExistsException(ErrorCodeIfs errorCodeIfs, String errorDescription) {
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorDescription;
    }

    public NickNameExistsException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
        super(throwable);
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorCodeIfs.getDescription();
    }

    public NickNameExistsException(ErrorCodeIfs errorCodeIfs, Throwable throwable,
        String errorDescription) {
        super(throwable);
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorDescription;
    }

}
