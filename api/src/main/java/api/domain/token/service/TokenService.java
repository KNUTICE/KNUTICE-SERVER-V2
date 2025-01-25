package api.domain.token.service;

import db.domain.token.DeviceTokenDocument;
import db.domain.token.DeviceTokenMongoRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final DeviceTokenMongoRepository deviceTokenMongoRepository;

    public Optional<DeviceTokenDocument> getDeviceToken(String deviceToken) {
        return deviceTokenMongoRepository.findById(deviceToken);
    }

    public Boolean saveDeviceToken(DeviceTokenDocument deviceTokenDocument) {
        DeviceTokenDocument savedDeviceToken = deviceTokenMongoRepository.save(deviceTokenDocument);
        return savedDeviceToken.getToken() != null;
    }

    public DeviceTokenDocument getDeviceTokenBy(String deviceToken) {
        // TODO 예외처리
        return deviceTokenMongoRepository.findById(deviceToken)
            .orElseThrow(() -> new RuntimeException(""));

    }
}
