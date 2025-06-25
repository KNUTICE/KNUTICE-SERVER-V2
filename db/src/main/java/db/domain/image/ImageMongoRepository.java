package db.domain.image;

import db.domain.image.enums.ImageKind;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageMongoRepository extends MongoRepository<ImageDocument, String> {

    Optional<ImageDocument> findFirstByImageKind(ImageKind imageKind);

}
