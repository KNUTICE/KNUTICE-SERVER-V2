package api.domain.tip.controller;

import api.domain.tip.business.TipBusiness;
import api.domain.tip.controller.model.TipSaveRequest;
import global.api.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tips")
public class TipApiController {

    private final TipBusiness tipBusiness;

    @PostMapping
    public Api<Boolean> saveTipInfo(@RequestBody @Valid Api<TipSaveRequest> tipSaveRequest) {
        return Api.OK(tipBusiness.saveTipInfo(tipSaveRequest.getBody()));
    }

    @DeleteMapping("/{tipId}")
    public Api<Boolean> deleteTip(@PathVariable String tipId) {
        return Api.OK(tipBusiness.deleteTipBy(tipId));
    }

}
