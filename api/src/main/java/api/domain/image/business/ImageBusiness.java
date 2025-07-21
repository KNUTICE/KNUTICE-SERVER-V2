package api.domain.image.business;

import api.common.error.ImageErrorCode;
import api.common.exception.image.ImageNotFoundException;
import api.domain.image.business.strategy.AbstractImageUploadStrategy;
import api.domain.image.business.strategy.ImageUploadStrategyFactory;
import api.domain.image.controller.model.ImageResponse;
import api.domain.image.converter.ImageConverter;
import api.domain.image.service.ImageService;
import api.domain.image.service.LocalImageStorageService;
import db.domain.image.ImageDocument;
import db.domain.image.enums.ImageKind;
import global.annotation.Business;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Business
@RequiredArgsConstructor
public class ImageBusiness {

    private final LocalImageStorageService localImageStorageService;
    private final ImageService imageService;
    private final ImageConverter imageConverter;
    private final ImageUploadStrategyFactory imageUploadStrategyFactory;

    /**
     * DEFAULT_IMAGE 는 하나만 존재 → 있으면 교체, 없으면 새로 저장 <br>
     * DEFAULT_IMAGE 의 서버 파일 이름은 **default.확장자** 로 제한한다. <br>
     * TIP_IMAGE 등은 단순 저장
     */
    public String uploadImage(MultipartFile multipartFile, ImageKind imageKind) {
        AbstractImageUploadStrategy strategy = imageUploadStrategyFactory.getStrategy(imageKind);
        return strategy.upload(multipartFile);
    }

    public List<ImageResponse> getImagesBy(ImageKind imageKind) {
        List<ImageDocument> imageDocuments = imageService.getImagesBy(imageKind);
        if (imageDocuments.isEmpty()) {
            throw new ImageNotFoundException(ImageErrorCode.IMAGE_NOT_FOUND);
        }
        return imageConverter.toResponse(imageDocuments);
    }

    public void deleteImage(String imageId) {
        imageService.getImageBy(imageId).ifPresentOrElse(imageDocument -> {
            localImageStorageService.deleteImageAsync(imageDocument);
            imageService.deleteImageMetaData(imageDocument);
        }, () -> {
            throw new ImageNotFoundException(ImageErrorCode.IMAGE_NOT_FOUND);
        });
    }

}
