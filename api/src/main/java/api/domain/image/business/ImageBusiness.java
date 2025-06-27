package api.domain.image.business;

import api.common.error.ImageErrorCode;
import api.common.exception.image.ImageNotFoundException;
import api.domain.image.controller.model.ImageResponse;
import api.domain.image.converter.ImageConverter;
import api.domain.image.service.ImageService;
import api.domain.image.service.LocalImageStorageService;
import api.domain.image.utils.FileUtils;
import db.domain.image.ImageDocument;
import db.domain.image.enums.ImageKind;
import global.annotation.Business;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Business
@RequiredArgsConstructor
public class ImageBusiness {

    private final LocalImageStorageService localImageStorageService;
    private final ImageService imageService;

    private final ImageConverter imageConverter;

    public void uploadImage(MultipartFile multipartFile, ImageKind imageKind) {
        if (multipartFile.isEmpty()) {
            throw new ImageNotFoundException(ImageErrorCode.IMAGE_NOT_FOUND);
        }

        if (imageKind == ImageKind.DEFAULT_IMAGE) {
            handleDefaultImageUpload(multipartFile, imageKind);
            return;
        }
        handleNewImageUpload(multipartFile, imageKind);

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
            imageService.deleteImageBy(imageDocument.getId());
        }, () -> {
            throw new ImageNotFoundException(ImageErrorCode.IMAGE_NOT_FOUND);
        });
    }

    private void handleDefaultImageUpload(MultipartFile multipartFile, ImageKind imageKind) {
        imageService.getImageByKind(imageKind).ifPresentOrElse(existingImage -> {
            log.info("기존 DEFAULT_IMAGE 존재 → 덮어쓰기");

            String fileName = existingImage.getServerName() + existingImage.getExtension();
            localImageStorageService.storeImageAsyncWithFileName(multipartFile, fileName);

            existingImage.setOriginalName(StringUtils.cleanPath(
                Objects.requireNonNull(multipartFile.getOriginalFilename())));
            existingImage.setExtension(FileUtils.getExtension(fileName));
            imageService.saveImageMetaData(existingImage);

        }, () -> {
            log.info("DEFAULT_IMAGE 없음 → 새로 저장");
            uploadAndSaveNewImage(multipartFile, imageKind);
        });
    }

    private void handleNewImageUpload(MultipartFile multipartFile, ImageKind imageKind) {
        log.info("새로운 이미지 저장 (DEFAULT 아님)");
        uploadAndSaveNewImage(multipartFile, imageKind);
    }

    private void uploadAndSaveNewImage(MultipartFile multipartFile, ImageKind imageKind) {
        localImageStorageService.storeImageAsync(multipartFile)
            .thenAccept(path -> {
                ImageDocument document = imageConverter.toDocument(multipartFile, imageKind, path);
                imageService.saveImageMetaData(document);
            });
    }

}
