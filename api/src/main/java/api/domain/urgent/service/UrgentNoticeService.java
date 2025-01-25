package api.domain.urgent.service;

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
            throw new RuntimeException("에러"); // TODO 예외처리
        }
        return urgentNoticeEntityList;
    }

}
