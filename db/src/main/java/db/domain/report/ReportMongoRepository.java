package db.domain.report;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportMongoRepository extends MongoRepository<ReportDocument, Long> {

}
