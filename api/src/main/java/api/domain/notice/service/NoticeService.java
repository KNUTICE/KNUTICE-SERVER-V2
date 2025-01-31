package api.domain.notice.service;

import api.common.error.NoticeErrorCode;
import api.common.exception.notice.NoticeNotFoundException;
import db.domain.notice.NoticeDocument;
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
        List<NoticeDocument> noticeSearchList = noticeQueryRepository.findSearchBy(qNoticeSearchDto);
        if (noticeSearchList.isEmpty()) {
            throw new NoticeNotFoundException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }
        return noticeSearchList;
    }
}
