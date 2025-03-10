package api.common.exception.topic;

import api.common.exception.BaseException;
import global.errorcode.ErrorCodeIfs;

public abstract class TopicException extends BaseException {
    public TopicException(ErrorCodeIfs errorCodeIfs) {
        super(errorCodeIfs);
    }

    public TopicException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
        super(errorCodeIfs, throwable);
    }

    public TopicException(ErrorCodeIfs errorCodeIfs, String description) {
        super(errorCodeIfs, description);
    }

    public TopicException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
        super(errorCodeIfs, description, throwable);
    }

    // 주제가 없는 경우
    public static class TopicNotFoundException extends TopicException {

        public TopicNotFoundException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public TopicNotFoundException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public TopicNotFoundException(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public TopicNotFoundException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }
}
