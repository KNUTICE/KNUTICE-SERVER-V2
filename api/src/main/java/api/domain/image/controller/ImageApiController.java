package api.domain.image.controller;

import api.domain.image.business.ImageBusiness;
import db.domain.image.enums.ImageKind;
import global.api.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageApiController {

    private final ImageBusiness imageBusiness;

    @PostMapping()
    public Api<Boolean> uploadImage(
        @RequestPart("image") MultipartFile multipartFile,
        @RequestParam ImageKind imageKind
    ) {
        imageBusiness.uploadImage(multipartFile, imageKind);
        return Api.OK(true);
    }

    @DeleteMapping()
    public Api<Boolean> deleteImage(@RequestParam String imageId) {
        imageBusiness.deleteImage(imageId);
        return Api.OK(true);
    }

}
