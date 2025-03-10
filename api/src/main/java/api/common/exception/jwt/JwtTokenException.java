package api.common.exception.jwt;
import api.common.exception.BaseException;
import global.errorcode.ErrorCodeIfs;

public abstract class JwtTokenException extends BaseException {

    public JwtTokenException(ErrorCodeIfs errorCodeIfs) {
        super(errorCodeIfs);
    }

    public JwtTokenException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
        super(errorCodeIfs, throwable);
    }

    public JwtTokenException(ErrorCodeIfs errorCodeIfs, String errorDescription) {
        super(errorCodeIfs, errorDescription);
    }

    public JwtTokenException(ErrorCodeIfs errorCodeIfs, String errorDescription, Throwable throwable) {
        super(errorCodeIfs, errorDescription, throwable);
    }

    // 토큰의 클레임 값이 잘못된 경우
    public static class JwtTokenMissingClaimException extends JwtTokenException {
        public JwtTokenMissingClaimException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public JwtTokenMissingClaimException(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public JwtTokenMissingClaimException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }

    // 토큰 작업 중 catch 하지 못한 에외인 경우
    public static class JwtUnknownException extends JwtTokenException {

        public JwtUnknownException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public JwtUnknownException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public JwtUnknownException(ErrorCodeIfs errorCodeIfs, String errorDescription) {
            super(errorCodeIfs, errorDescription);
        }

        public JwtUnknownException(ErrorCodeIfs errorCodeIfs, String errorDescription, Throwable throwable) {
            super(errorCodeIfs, errorDescription, throwable);
        }
    }

    // 토큰이 만료된 경우
    public static class JwtTokenExpiredException extends JwtTokenException {

        public JwtTokenExpiredException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public JwtTokenExpiredException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public JwtTokenExpiredException(ErrorCodeIfs errorCodeIfs, String errorDescription) {
            super(errorCodeIfs, errorDescription);
        }

        public JwtTokenExpiredException(ErrorCodeIfs errorCodeIfs, String errorDescription, Throwable throwable) {
            super(errorCodeIfs, errorDescription, throwable);
        }
    }

    // 토큰의 서명이 올바르지 않은 경우
    public static class JwtTokenSignatureException extends JwtTokenException {

        public JwtTokenSignatureException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public JwtTokenSignatureException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public JwtTokenSignatureException(ErrorCodeIfs errorCodeIfs, String errorDescription) {
            super(errorCodeIfs, errorDescription);
        }

        public JwtTokenSignatureException(ErrorCodeIfs errorCodeIfs, String errorDescription, Throwable throwable) {
            super(errorCodeIfs, errorDescription, throwable);
        }
    }

    // 토큰이 일치하지 않는 경우
    public static class JwtTokenInvalidException extends JwtTokenException {

        public JwtTokenInvalidException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public JwtTokenInvalidException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public JwtTokenInvalidException(ErrorCodeIfs errorCodeIfs, String errorDescription) {
            super(errorCodeIfs, errorDescription);
        }

        public JwtTokenInvalidException(ErrorCodeIfs errorCodeIfs, String errorDescription, Throwable throwable) {
            super(errorCodeIfs, errorDescription, throwable);
        }
    }

    // Jwt document 예외인 경우
    public static class JwtTokenDocumentException extends JwtTokenException {

        public JwtTokenDocumentException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public JwtTokenDocumentException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public JwtTokenDocumentException(ErrorCodeIfs errorCodeIfs, String errorDescription) {
            super(errorCodeIfs, errorDescription);
        }

        public JwtTokenDocumentException(ErrorCodeIfs errorCodeIfs, String errorDescription, Throwable throwable) {
            super(errorCodeIfs, errorDescription, throwable);
        }
    }
}
