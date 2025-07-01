package api.domain.notice.business;

import api.common.error.NoticeErrorCode;
import api.common.exception.notice.NoticeExistsException;
import api.common.exception.notice.NoticeNotFoundException;
import api.domain.notice.controller.model.request.NoticeSaveRequest;
import api.domain.notice.controller.model.request.NoticeUpdateRequest;
import api.domain.notice.controller.model.response.LatestThreeNoticeResponse;
import api.domain.notice.controller.model.request.NoticeRequest;
import api.domain.notice.controller.model.response.NoticeResponse;
import api.domain.notice.controller.model.request.NoticeSyncRequest;
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
        List<NoticeDocument> employmentNews = noticeService.getLatestThreeNoticeBy(
            NoticeMapper.EMPLOYMENT_NEWS);

        return noticeConverter.toResponse(generalNews, scholarshipNews, eventNews, academicNews, employmentNews);
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

    /**
     * 관리자가 새로운 공지사항을 저장할 때 사용하는 메서드.
     * 중복된 nttId가 존재할 경우 예외를 발생시킴.
     */
    public Boolean saveNotice(NoticeSaveRequest noticeSaveRequest) {
        if (noticeService.existsNoticeBy(noticeSaveRequest.getNttId())) {
            throw new NoticeExistsException(NoticeErrorCode.NOTICE_ALREADY_EXISTS);
        }

        NoticeDocument noticeDocument = noticeConverter.toDocument(noticeSaveRequest);
        return noticeService.saveNoticeBy(noticeDocument);
    }


    /**
     * 관리자가 공지사항을 삭제할 때 사용하는 메서드.
     * 존재하지 않는 nttId일 경우 예외를 발생시킴.
     */
    public Boolean deleteNotice(Long nttId) {
        if (!noticeService.existsNoticeBy(nttId)) {
            throw new NoticeNotFoundException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }
        noticeService.deleteNotice(nttId);
        return true;
    }

     /**
     * 관리자가 기존 공지사항을 수정할 때 사용하는 메서드.
     */
    public Boolean updateNotice(NoticeUpdateRequest noticeUpdateRequest) {
        NoticeDocument noticeDocument = noticeService.getNoticeBy(noticeUpdateRequest.getNttId());
        noticeConverter.updateDocument(noticeDocument, noticeUpdateRequest);
        return noticeService.saveNoticeBy(noticeDocument);
    }

}
