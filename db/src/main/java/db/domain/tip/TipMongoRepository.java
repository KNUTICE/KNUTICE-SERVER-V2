package db.domain.tip;

import global.utils.DeviceType;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TipMongoRepository extends MongoRepository<TipDocument, String> {

    List<TipDocument> findAllByDeviceTypeOrderByRegisteredAtDesc(DeviceType deviceType);

}
