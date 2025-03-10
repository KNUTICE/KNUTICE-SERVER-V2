package api.common.exception.fcm;

import api.common.exception.BaseException;
import global.errorcode.ErrorCodeIfs;

public abstract class FcmException extends BaseException {
    public FcmException(ErrorCodeIfs errorCodeIfs) {
        super(errorCodeIfs);
    }

    public FcmException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
        super(errorCodeIfs, throwable);
    }

    public FcmException(ErrorCodeIfs errorCodeIfs, String description) {
        super(errorCodeIfs, description);
    }

    public FcmException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
        super(errorCodeIfs, description, throwable);
    }

    // fcmToken 을 찾지 못한 경우
    public static class FcmTokenNotFoundException extends FcmException {

        public FcmTokenNotFoundException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public FcmTokenNotFoundException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public FcmTokenNotFoundException(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public FcmTokenNotFoundException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }
}

