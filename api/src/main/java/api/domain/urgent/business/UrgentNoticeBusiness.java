package api.domain.urgent.business;

import api.common.error.UrgentNoticeErrorCode;
import api.common.exception.urgent.UrgentNoticeNotFoundException;
import api.domain.urgent.controller.model.UrgentNoticeResponse;
import api.domain.urgent.converter.UrgentNoticeConverter;
import api.domain.urgent.service.UrgentNoticeService;
import db.domain.urgent.UrgentNoticeDocument;
import global.annotation.Business;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class UrgentNoticeBusiness {

    private final UrgentNoticeService urgentNoticeService;

    private final UrgentNoticeConverter urgentNoticeConverter;

    public UrgentNoticeResponse getUrgentNotice() {
        List<UrgentNoticeDocument> UrgentNoticeDocument = urgentNoticeService.getUrgentNoticeList();

        if (UrgentNoticeDocument.isEmpty()) {
            throw new UrgentNoticeNotFoundException(UrgentNoticeErrorCode.URGENT_NOTICE_NOT_FOUND);
        }

        return urgentNoticeConverter.toResponse(UrgentNoticeDocument.get(0)); // 단일 공지 반환
    }

}
