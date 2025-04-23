package api.domain.notice.business;

import api.domain.notice.controller.model.latestnotice.LatestThreeNoticeResponse;
import api.domain.notice.controller.model.noticelist.NoticeRequest;
import api.domain.notice.controller.model.noticelist.NoticeResponse;
import api.domain.notice.controller.model.sync.NoticeSyncRequest;
import api.domain.notice.converter.NoticeConverter;
import api.domain.notice.service.NoticeService;
import db.domain.notice.NoticeDocument;
import db.domain.notice.dto.QNoticeDto;
import global.annotation.Business;
import global.utils.NoticeMapper;
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

        List<NoticeDocument> noticeList = noticeService.getNoticeList(qNoticeDto);

        return noticeConverter.toResponse(noticeList);
    }

    public LatestThreeNoticeResponse getLatestThreeNotice() {
        List<NoticeDocument> generalNews = noticeService.getLatestThreeNoticeBy(
            NoticeMapper.GENERAL_NEWS);
        List<NoticeDocument> scholarshipNews = noticeService.getLatestThreeNoticeBy(
            NoticeMapper.SCHOLARSHIP_NEWS);
        List<NoticeDocument> eventNews = noticeService.getLatestThreeNoticeBy(
            NoticeMapper.EVENT_NEWS);
        List<NoticeDocument> academicNews = noticeService.getLatestThreeNoticeBy(
            NoticeMapper.ACADEMIC_NEWS);

        return noticeConverter.toResponse(generalNews, scholarshipNews, eventNews, academicNews);
    }

    public NoticeResponse getNoticeBy(Long nttId) {
        NoticeDocument noticeDocument = noticeService.getNoticeBy(nttId);
        return noticeConverter.toResponse(noticeDocument);
    }

    public List<NoticeResponse> getNoticeList(NoticeSyncRequest noticeSyncRequest) {
        List<NoticeDocument> noticeList = noticeService.getNoticeList(
            noticeSyncRequest.getNttIdList());
        return noticeConverter.toResponse(noticeList);
    }

}
