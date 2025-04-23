package db.domain.token.fcm;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FcmTokenMongoRepository extends MongoRepository<FcmTokenDocument, String> {

    void deleteAllByFcmTokenIn(List<String> failedTokens);

    List<FcmTokenDocument> findAllByGeneralNewsTopicTrue();
    List<FcmTokenDocument> findAllByEventNewsTopicTrue();
    List<FcmTokenDocument> findAllByScholarshipNewsTopicTrue();
    List<FcmTokenDocument> findAllByAcademicNewsTopicTrue();
    List<FcmTokenDocument> findAllByEmploymentNewsTopicTrue();

}