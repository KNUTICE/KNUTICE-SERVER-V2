package api.domain.report.converter;

import api.domain.report.controller.model.ReportRequest;
import db.domain.report.ReportDocument;
import global.annotation.Converter;
import java.time.LocalDateTime;

@Converter
public class ReportConverter {

    public ReportDocument toEntity(ReportRequest reportRequest) {
        return ReportDocument.builder()
            .token(reportRequest.getToken())
            .content(reportRequest.getContent())
            .clientType(reportRequest.getClientType())
            .deviceName(reportRequest.getDeviceName())
            .version(reportRequest.getVersion())
            .registeredAt(LocalDateTime.now())
            .build();
    }

}