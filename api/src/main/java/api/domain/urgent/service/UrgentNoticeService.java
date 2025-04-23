package api.domain.urgent.service;

import api.common.error.UrgentNoticeErrorCode;
import api.common.exception.urgent.UrgentNoticeNotFoundException;
import db.domain.urgent.UrgentNoticeDocument;
import db.domain.urgent.UrgentNoticeMongoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrgentNoticeService {

    private final UrgentNoticeMongoRepository urgentNoticeMongoRepository;

    public List<UrgentNoticeDocument> getUrgentNoticeList() {
        List<UrgentNoticeDocument> urgentNoticeEntityList = urgentNoticeMongoRepository.findAll();

        if (urgentNoticeEntityList.isEmpty()) {
            throw new UrgentNoticeNotFoundException(UrgentNoticeErrorCode.URGENT_NOTICE_NOT_FOUND);
        }
        return urgentNoticeEntityList;
    }

}
