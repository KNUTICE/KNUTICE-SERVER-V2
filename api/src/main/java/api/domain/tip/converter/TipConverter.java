package api.domain.tip.converter;

import api.domain.tip.controller.model.TipResponse;
import api.domain.tip.controller.model.TipSaveRequest;
import db.domain.tip.TipDocument;
import global.annotation.Converter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class TipConverter {

    public TipDocument toDocument(TipSaveRequest tipSaveRequest) {
        return TipDocument.builder()
            .title(tipSaveRequest.getTitle())
            .url(tipSaveRequest.getUrl())
            .deviceType(tipSaveRequest.getDeviceType())
            .registeredAt(LocalDateTime.now())
            .build();
    }

    public List<TipResponse> toResponse(List<TipDocument> tipDocuments) {
        return tipDocuments.stream().map(tipDocument ->
            TipResponse.builder()
                .id(tipDocument.getId())
                .title(tipDocument.getTitle())
                .url(tipDocument.getUrl())
                .registeredAt(tipDocument.getRegisteredAt())
                .deviceType(tipDocument.getDeviceType())
                .build()
        ).collect(Collectors.toList());
    }

}
