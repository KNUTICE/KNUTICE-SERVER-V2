package api.infra.secon;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FcmTokenMongoSeconRepository extends MongoRepository<FcmTokenSeconDocument, String> {


}
