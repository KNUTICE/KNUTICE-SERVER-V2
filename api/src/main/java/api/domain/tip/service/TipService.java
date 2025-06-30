package api.domain.tip.service;

import db.domain.tip.TipDocument;
import db.domain.tip.TipMongoRepository;
import global.utils.DeviceType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TipService {

    private final TipMongoRepository tipMongoRepository;

    public boolean saveTipInfo(TipDocument tipDocument) {
        return tipMongoRepository.save(tipDocument).getId() != null;
    }

    public List<TipDocument> getTipsBy(DeviceType deviceType) {
        return tipMongoRepository.findAllByDeviceTypeOrderByRegisteredAtDesc(deviceType);
    }

    public Boolean existsTip(String tipId) {
        return tipMongoRepository.existsById(tipId);
    }

    public void deleteTipBy(String tipId) {
        tipMongoRepository.deleteById(tipId);
    }

}
