package api.common.exception.report;

import api.common.exception.BaseException;
import global.errorcode.ErrorCodeIfs;

public abstract class ReportException extends BaseException
{

    public ReportException(ErrorCodeIfs errorCodeIfs) {
        super(errorCodeIfs);
    }

    public ReportException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
        super(errorCodeIfs, throwable);
    }

    public ReportException(ErrorCodeIfs errorCodeIfs, String description) {
        super(errorCodeIfs, description);
    }

    public ReportException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
        super(errorCodeIfs, description, throwable);
    }

    // 리포트가 존재하지 않는 경우
    public static class ReportNotFoundException extends ReportException {

        public ReportNotFoundException(ErrorCodeIfs errorCodeIfs) {
            super(errorCodeIfs);
        }

        public ReportNotFoundException(ErrorCodeIfs errorCodeIfs, Throwable throwable) {
            super(errorCodeIfs, throwable);
        }

        public ReportNotFoundException(ErrorCodeIfs errorCodeIfs, String description) {
            super(errorCodeIfs, description);
        }

        public ReportNotFoundException(ErrorCodeIfs errorCodeIfs, String description, Throwable throwable) {
            super(errorCodeIfs, description, throwable);
        }
    }
}

