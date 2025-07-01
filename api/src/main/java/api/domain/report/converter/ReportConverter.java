package api.domain.report.converter;

import api.domain.report.controller.model.ReportDetailResponse;
import api.domain.report.controller.model.ReportSimpleResponse;
import api.domain.report.controller.model.ReportRequest;
import db.domain.report.ReportDocument;
import global.annotation.Converter;
import java.time.LocalDateTime;
import java.util.List;

@Converter
public class ReportConverter {

    public ReportDocument toDocument(ReportRequest reportRequest) {
        return ReportDocument.builder()
            .fcmToken(reportRequest.getFcmToken())
            .content(reportRequest.getContent())
            .clientType(reportRequest.getClientType())
            .deviceName(reportRequest.getDeviceName())
            .version(reportRequest.getVersion())
            .registeredAt(LocalDateTime.now())
            .build();
    }


    public ReportSimpleResponse toResponse(ReportDocument reportDocument) {
        return ReportSimpleResponse.builder()
            .reportId(reportDocument.getId())
            .content(reportDocument.getContent())
            .registeredAt(reportDocument.getRegisteredAt())
            .build();
    }

    public List<ReportSimpleResponse> toListResponse(List<ReportDocument> reportDocumentList) {
        return reportDocumentList.stream()
            .map(this::toResponse)
            .toList();
    }

    public ReportDetailResponse toDetailResponse(ReportDocument reportDocument) {
        return ReportDetailResponse.builder()
            .reportId(reportDocument.getId())
            .fcmToken(reportDocument.getFcmToken())
            .content(reportDocument.getContent())
            .clientType(reportDocument.getClientType())
            .deviceName(reportDocument.getDeviceName())
            .version(reportDocument.getVersion())
            .registeredAt(reportDocument.getRegisteredAt())
            .build();
    }

}