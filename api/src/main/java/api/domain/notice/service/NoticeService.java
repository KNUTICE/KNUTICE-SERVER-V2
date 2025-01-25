package api.domain.notice.service;

import db.domain.notice.NoticeDocument;
import db.domain.notice.NoticeQueryRepository;
import db.domain.notice.dto.QNoticeDto;
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
            throw new RuntimeException("예외"); // TODO 예외
        }

        return noticeList;
    }

}
