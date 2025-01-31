package db.domain.token.jwt;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface JwtTokenMongoRepository extends MongoRepository<JwtTokenDocument, String> {

}