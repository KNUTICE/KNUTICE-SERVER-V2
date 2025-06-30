package api.domain.image.business;

import api.common.error.ImageErrorCode;
import api.common.exception.image.ImageNotFoundException;
import api.common.exception.image.ImageStorageWriteException;
import api.domain.image.controller.model.ImageResponse;
import api.domain.image.converter.ImageConverter;
import api.domain.image.service.ImageService;
import api.domain.image.service.LocalImageStorageService;
import api.domain.image.utils.FileUtils;
import db.domain.image.ImageDocument;
import db.domain.image.enums.ImageKind;
import global.annotation.Business;
import java.io.IOException;
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

        byte[] imageData;
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try {
            imageData = multipartFile.getBytes();
        } catch (IOException e) {
            throw new ImageStorageWriteException(ImageErrorCode.IMAGE_STORAGE_FAILED);
        }

        if (imageKind == ImageKind.DEFAULT_IMAGE) {
            handleDefaultImageUpload(imageData, originalFilename);
            return;
        }
        handleNewImageUpload(imageData, originalFilename, imageKind);

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

    private void handleDefaultImageUpload(byte[] imageData, String newOriginalFilename) {
        imageService.getImageByKind(ImageKind.DEFAULT_IMAGE).ifPresentOrElse(existingImage -> {
            log.info("기존 DEFAULT_IMAGE 존재 → 덮어쓰기");

            String newFileExtension = FileUtils.getExtension(newOriginalFilename);
            String replaceFilename;

            if (existingImage.getExtension().equals(newFileExtension)) {
                replaceFilename = existingImage.getServerName() + existingImage.getExtension();
            } else { // 확장자가 다른 경우 기존 이미지 제거
                replaceFilename = existingImage.getServerName() + newFileExtension;
                localImageStorageService.deleteImageAsync(existingImage);
            }

            localImageStorageService.replaceImageAsync(imageData, replaceFilename)
                .thenRun(() -> {
                    existingImage.setOriginalName(FileUtils.getFileOfName(newOriginalFilename));
                    existingImage.setExtension(newFileExtension);
                    imageService.saveImageMetaData(existingImage);
                });
        }, () -> {
            log.info("DEFAULT_IMAGE 없음 → 새로 저장");
            uploadAndSaveNewImage(imageData, newOriginalFilename, ImageKind.DEFAULT_IMAGE);
        });
    }

    private void handleNewImageUpload(byte[] imageData, String originalFilename, ImageKind imageKind) {
        log.info("새로운 이미지 저장 (DEFAULT 아님)");
        uploadAndSaveNewImage(imageData, originalFilename, imageKind);
    }

    private void uploadAndSaveNewImage(byte[] imageData, String originalFilename, ImageKind imageKind) {
        localImageStorageService.storeImageAsync(imageData, originalFilename)
            .thenAccept(path -> {
                ImageDocument document = imageConverter.toDocument(originalFilename, imageKind, path);
                imageService.saveImageMetaData(document);
            });
    }

}
