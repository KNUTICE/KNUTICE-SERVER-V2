package api.common.exception.notice;

import api.common.exception.BaseException;
import global.errorcode.ErrorCodeIfs;

public abstract class NoticeException extends BaseException {
    public NoticeException(ErrorCodeIfs errorCodeIfs) {
        super(errorCodeIfs);
    }

    public NoticeException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
        super(errorCodeIfs, throwable);
    }

    public NoticeException(ErrorCodeIfs errorCodeIfs, String description) {
        super(errorCodeIfs, description);
    }

    public NoticeException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
        super(errorCodeIfs, description, throwable);
    }


    // 공지가 이미 존재하는 경우
    public static class NoticeExistsException extends NoticeException {

        public NoticeExistsException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public NoticeExistsException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public NoticeExistsException(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public NoticeExistsException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }

    // 공지를 찾을 수 없는 경우
    public static class NoticeNotFoundException extends NoticeException {

        public NoticeNotFoundException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public NoticeNotFoundException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public NoticeNotFoundException(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public NoticeNotFoundException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }

    // 긴급공지사항을 찾을 수 없는 경우
    public static class UrgentNoticeNotFoundException extends NoticeException {

        public UrgentNoticeNotFoundException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public UrgentNoticeNotFoundException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public UrgentNoticeNotFoundException(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public UrgentNoticeNotFoundException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }
}
