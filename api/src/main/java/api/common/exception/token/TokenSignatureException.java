package api.common.exception.token;

import global.errorcode.ErrorCodeIfs;

public class TokenSignatureException extends RuntimeException{

    private final ErrorCodeIfs errorCodeIfs;
    private final String description;

    public TokenSignatureException(ErrorCodeIfs errorCodeIfs) {
        super(errorCodeIfs.getDescription());
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorCodeIfs.getDescription();
    }

    public TokenSignatureException(ErrorCodeIfs errorCodeIfs, String errorDescription) {
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorDescription;
    }

    public TokenSignatureException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
        super(throwable);
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorCodeIfs.getDescription();
    }

    public TokenSignatureException(ErrorCodeIfs errorCodeIfs, Throwable throwable,
        String errorDescription) {
        super(throwable);
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorDescription;
    }

}
