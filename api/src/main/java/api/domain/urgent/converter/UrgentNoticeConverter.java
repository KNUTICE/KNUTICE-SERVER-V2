package api.domain.urgent.converter;

import api.domain.admin.controller.model.request.UrgentNoticeSaveRequest;
import api.domain.urgent.controller.model.UrgentNoticeResponse;
import db.domain.urgent.UrgentNoticeDocument;
import global.annotation.Converter;
import java.time.LocalDate;

@Converter
public class UrgentNoticeConverter {

    public UrgentNoticeResponse toResponse(UrgentNoticeDocument UrgentNoticeDocument) {
        return UrgentNoticeResponse.builder()
            .title(UrgentNoticeDocument.getTitle())
            .content(UrgentNoticeDocument.getContent())
            .contentUrl(UrgentNoticeDocument.getContentUrl())
            .registeredAt(UrgentNoticeDocument.getRegisteredAt())
            .build();
    }

    public UrgentNoticeDocument toDocument(UrgentNoticeSaveRequest urgentNoticeSaveRequest) {
        return UrgentNoticeDocument.builder()
            .title(urgentNoticeSaveRequest.getTitle())
            .content(urgentNoticeSaveRequest.getContent())
            .contentUrl(urgentNoticeSaveRequest.getContentUrl())
            .registeredAt(LocalDate.now())
            .build();
    }

}
