package db.domain.token;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceTokenMongoRepository extends MongoRepository<DeviceTokenDocument, String> {

    void deleteAllByTokenIn(List<String> failedTokens);

    List<DeviceTokenDocument> findAllByGeneralNewsTopicTrue();
    List<DeviceTokenDocument> findAllByEventNewsTopicTrue();
    List<DeviceTokenDocument> findAllByScholarshipNewsTopicTrue();
    List<DeviceTokenDocument> findAllByAcademicNewsTopicTrue();

}