package db.domain.notice;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoticeMongoRepository extends MongoRepository<NoticeDocument, Long> {

}
