package db.domain.urgent;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrgentNoticeMongoRepository extends MongoRepository<UrgentNoticeDocument, Long> {

}
