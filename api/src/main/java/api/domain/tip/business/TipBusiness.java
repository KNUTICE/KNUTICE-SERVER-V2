package api.domain.tip.business;

import api.common.error.TipErrorCode;
import api.common.exception.tip.TipNotFoundException;
import api.domain.tip.controller.model.TipResponse;
import api.domain.tip.controller.model.TipSaveRequest;
import api.domain.tip.converter.TipConverter;
import api.domain.tip.service.TipService;
import db.domain.tip.TipDocument;
import global.annotation.Business;
import global.utils.DeviceType;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class TipBusiness {

    private final TipService tipService;
    private final TipConverter tipConverter;

    public Boolean saveTipInfo(TipSaveRequest tipSaveRequest) {
        return tipService.saveTipInfo(tipConverter.toDocument(tipSaveRequest));
    }

    public List<TipResponse> getTipsBy(DeviceType deviceType) {
        List<TipDocument> tipDocuments = tipService.getTipsBy(deviceType);

        if (tipDocuments.isEmpty()) {
            throw new TipNotFoundException(TipErrorCode.TIP_NOT_FOUND);
        }

        return tipConverter.toResponse(tipDocuments);

    }

    public Boolean deleteTipBy(String tipId) {
        if(!tipService.existsTip(tipId)) {
            throw new TipNotFoundException(TipErrorCode.TIP_NOT_FOUND);
        }
        tipService.deleteTipBy(tipId);
        return true;
    }

}
