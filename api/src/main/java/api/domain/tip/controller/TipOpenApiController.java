package api.domain.tip.controller;

import api.domain.tip.business.TipBusiness;
import api.domain.tip.controller.model.TipResponse;
import global.api.Api;
import global.utils.DeviceType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api/tips")
public class TipOpenApiController {

    private final TipBusiness tipBusiness;

    @GetMapping
    public Api<List<TipResponse>> getTips(@RequestParam DeviceType deviceType) {
        return Api.OK(tipBusiness.getTipsBy(deviceType));
    }

}
