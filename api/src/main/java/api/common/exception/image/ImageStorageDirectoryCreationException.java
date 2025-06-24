package api.common.exception.image;

import global.errorcode.ErrorCodeIfs;
import lombok.Getter;

@Getter
public class ImageStorageDirectoryCreationException extends RuntimeException {

    private final ErrorCodeIfs errorCodeIfs;
    private final String description;

    public ImageStorageDirectoryCreationException(ErrorCodeIfs errorCodeIfs) {
        super(errorCodeIfs.getDescription());
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorCodeIfs.getDescription();
    }

    public ImageStorageDirectoryCreationException(ErrorCodeIfs errorCodeIfs, String errorDescription) {
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorDescription;
    }

    public ImageStorageDirectoryCreationException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
        super(throwable);
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorCodeIfs.getDescription();
    }

    public ImageStorageDirectoryCreationException(ErrorCodeIfs errorCodeIfs, Throwable throwable,
        String errorDescription) {
        super(throwable);
        this.errorCodeIfs = errorCodeIfs;
        this.description = errorDescription;
    }

}
