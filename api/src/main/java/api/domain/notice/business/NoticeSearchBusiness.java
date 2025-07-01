package api.domain.notice.business;

import api.domain.notice.controller.model.response.NoticeResponse;
import api.domain.notice.controller.model.request.NoticeSearchRequest;
import api.domain.notice.converter.NoticeConverter;
import api.domain.notice.service.NoticeService;
import db.domain.notice.NoticeDocument;
import db.domain.notice.dto.QNoticeSearchDto;
import global.annotation.Business;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@Business
@RequiredArgsConstructor
public class NoticeSearchBusiness {

    private final NoticeService noticeService;
    private final NoticeConverter noticeConverter;


    public List<NoticeResponse> getSearchBy(NoticeSearchRequest noticeSearchRequest,
        Pageable page) {

        QNoticeSearchDto QNoticeSearchDto = noticeConverter.toDto(noticeSearchRequest, page);

        List<NoticeDocument> noticeSearchList = noticeService.getNoticeSearchList(QNoticeSearchDto);

        return noticeConverter.toResponse(noticeSearchList);
    }
}
