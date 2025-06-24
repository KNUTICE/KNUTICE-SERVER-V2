package api.domain.notice.service;

import api.common.error.NoticeErrorCode;
import api.common.exception.notice.NoticeNotFoundException;
import db.domain.notice.NoticeDocument;
import db.domain.notice.NoticeMongoRepository;
import db.domain.notice.NoticeQueryRepository;
import db.domain.notice.dto.QNoticeDto;
import db.domain.notice.dto.QNoticeSearchDto;
import global.utils.NoticeMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeQueryRepository noticeQueryRepository;
    private final NoticeMongoRepository noticeMongoRepository;

    public List<NoticeDocument> getNoticeList(QNoticeDto qNoticeDto) {
        List<NoticeDocument> noticeList = noticeQueryRepository.findNoticeBy(qNoticeDto);

        if (noticeList.isEmpty()) {
            throw new NoticeNotFoundException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }

        return noticeList;
    }

    public List<NoticeDocument> getLatestThreeNoticeBy(NoticeMapper noticeName) {
        return noticeQueryRepository.findLatestThreeNoticeBy(noticeName);
    }

    public List<NoticeDocument> getNoticeSearchList(QNoticeSearchDto qNoticeSearchDto) {
        List<NoticeDocument> noticeSearchList = noticeQueryRepository.findSearchBy(
            qNoticeSearchDto);
        if (noticeSearchList.isEmpty()) {
            throw new NoticeNotFoundException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }
        return noticeSearchList;
    }

    public NoticeDocument getNoticeBy(Long nttId) {
        return noticeMongoRepository.findById(nttId)
            .orElseThrow(() -> new NoticeNotFoundException(NoticeErrorCode.NOTICE_NOT_FOUND));
    }

    public Boolean existsNoticeBy(Long nttId) {
        return noticeMongoRepository.existsById(nttId);
    }

    public List<NoticeDocument> getNoticeList(List<Long> nttIdList) {
        List<NoticeDocument> noticeDocumentList = noticeMongoRepository.findAllById(nttIdList);
        if (noticeDocumentList.isEmpty()) {
            throw new NoticeNotFoundException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }
        return noticeDocumentList;
    }

    /**
     * Admin Service
     * @param noticeDocument
     * @return
     */
    public Boolean saveNoticeBy(NoticeDocument noticeDocument) {
        NoticeDocument savedNoticeDocument = noticeMongoRepository.save(noticeDocument);
        return savedNoticeDocument.getNttId() != null;
    }

    public void deleteNotice(Long nttId) {
        noticeMongoRepository.deleteById(nttId);
    }


}
