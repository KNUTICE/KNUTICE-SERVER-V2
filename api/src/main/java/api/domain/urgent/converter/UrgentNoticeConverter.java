package api.domain.urgent.converter;

import api.domain.urgent.controller.model.UrgentNoticeResponse;
import db.domain.urgent.UrgentNoticeDocument;
import global.annotation.Converter;

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

}
