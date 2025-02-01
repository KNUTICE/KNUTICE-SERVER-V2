package api.common.exception;

import api.common.error.ReportErrorCode;
import api.common.exception.report.ReportNotFoundException;
import global.api.Api;
import global.errorcode.ErrorCodeIfs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ReportExceptionHandler {

    @ExceptionHandler(value = ReportNotFoundException.class)
    public ResponseEntity<Api<ErrorCodeIfs>> NotFoundReportException(ReportNotFoundException e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Api.ERROR(ReportErrorCode.REPORT_NOT_FOUND));
    }

}
