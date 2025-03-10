package api.common.exception.user;

import api.common.exception.BaseException;
import global.errorcode.ErrorCodeIfs;

public abstract class UserException extends BaseException {
    public UserException(ErrorCodeIfs errorCodeIfs) {
        super(errorCodeIfs);
    }

    public UserException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
        super(errorCodeIfs, throwable);
    }

    public UserException(ErrorCodeIfs errorCodeIfs, String description) {
        super(errorCodeIfs, description);
    }

    public UserException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
        super(errorCodeIfs, description, throwable);
    }

    // 이메일이 이미 존재하는 경우
    public static class EmailExistsException extends UserException{
        public EmailExistsException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public EmailExistsException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public EmailExistsException(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public EmailExistsException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }

    // 패스워드가 일치하지 않는 경우
    public static class PasswordMismatchException extends UserException {
        public PasswordMismatchException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public PasswordMismatchException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public PasswordMismatchException(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public PasswordMismatchException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }

    // 회원이 이미 존재하는 경우
    public static class UserExistsException extends UserException {
        public UserExistsException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public UserExistsException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public UserExistsException(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public UserExistsException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }

    // 회원이 없는 경우
    public static class UserNotFoundException extends UserException {
        public UserNotFoundException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public UserNotFoundException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public UserNotFoundException(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public UserNotFoundException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }

    // 회원권한이 없는 경우
    public static class UserNotPermitted extends UserException {
        public UserNotPermitted(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public UserNotPermitted(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public UserNotPermitted(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public UserNotPermitted(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }
}

