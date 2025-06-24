package db.domain.user;

import db.domain.user.enums.UserStatus;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserDocument, String> {

    Boolean existsByAccount_Email(String email);

    Optional<UserDocument> findFirstByAccount_Email(String email);

}
