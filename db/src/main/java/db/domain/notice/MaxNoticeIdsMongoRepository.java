package db.domain.notice;

import global.utils.NoticeMapper;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MaxNoticeIdsMongoRepository extends MongoRepository<MaxNoticeIdsDocument, NoticeMapper> {

}