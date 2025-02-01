package api.domain.report.converter;

import api.domain.admin.controller.model.response.ReportDetailResponse;
import api.domain.admin.controller.model.response.ReportListResponse;
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


    public ReportListResponse toResponse(ReportDocument reportDocument) {
        return ReportListResponse.builder()
            .reportId(reportDocument.getId())
            .content(reportDocument.getContent())
            .registeredAt(reportDocument.getRegisteredAt())
            .build();
    }

    public List<ReportListResponse> toListResponse(List<ReportDocument> reportDocumentList) {
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