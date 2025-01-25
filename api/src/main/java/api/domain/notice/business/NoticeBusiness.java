package api.domain.notice.business;

import api.domain.notice.controller.model.noticelist.NoticeRequest;
import api.domain.notice.controller.model.noticelist.NoticeResponse;
import api.domain.notice.converter.NoticeConverter;
import api.domain.notice.service.NoticeService;
import db.domain.notice.dto.QNoticeDto;
import global.annotation.Business;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@Business
@RequiredArgsConstructor
public class NoticeBusiness {

    private final NoticeService noticeService;

    private final NoticeConverter noticeConverter;

    public List<NoticeResponse> getNoticeList(NoticeRequest noticeRequest, Pageable page) {

        QNoticeDto qNoticeDto = noticeConverter.toDto(noticeRequest, page);

        List<NoticeProjection> noticeList = noticeService.getNoticeList(qNoticeDto);

        return noticeConverter.toResponse(noticeList);
    }
}
