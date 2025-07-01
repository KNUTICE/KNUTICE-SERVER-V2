package api.domain.urgent.business;

import api.common.error.UrgentNoticeErrorCode;
import api.common.exception.urgent.UrgentNoticeNotFoundException;
import api.domain.urgent.controller.model.UrgentNoticeSaveRequest;
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


    /**
     * 관리자가 새로운 긴급공지를 저장할 때 사용하는 메서드.
     * 존재하는 긴급공지를 지우고 새로 저장함
     */
    public Boolean saveUrgentNotice(UrgentNoticeSaveRequest urgentNoticeSaveRequest) {
        // 기존 긴급 공지 모두 삭제
        urgentNoticeService.deleteAllUrgentNotice();

        UrgentNoticeDocument urgentNoticeDocument = urgentNoticeConverter.toDocument(
            urgentNoticeSaveRequest);
        return urgentNoticeService.saveUrgentNoticeBy(urgentNoticeDocument);
    }

    /**
     * 관리자가 긴급 공지사항을 삭제할 때 사용하는 메서드.
     */
    public Boolean deleteUrgentNotice() {
        return urgentNoticeService.deleteAllUrgentNotice();
    }

}
