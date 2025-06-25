package api.domain.image.controller;

import api.domain.image.business.ImageBusiness;
import api.domain.image.controller.model.ImageResponse;
import db.domain.image.enums.ImageKind;
import global.api.Api;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api/images")
public class ImageOpenApiController {

    private final ImageBusiness imageBusiness;

    @GetMapping()
    public Api<List<ImageResponse>> getImages(@RequestParam ImageKind imageKind) {
        return Api.OK(imageBusiness.getImagesBy(imageKind));
    }

}
