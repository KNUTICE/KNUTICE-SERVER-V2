package api.domain.image.business;

import api.common.error.ImageErrorCode;
import api.common.exception.image.ImageNotFoundException;
import api.common.exception.image.InvalidImageExtensionException;
import api.domain.image.controller.model.ImageResponse;
import api.domain.image.converter.ImageConverter;
import api.domain.image.service.ImageService;
import api.domain.image.service.LocalImageStorageService;
import api.domain.image.utils.FileUtils;
import db.domain.image.ImageDocument;
import db.domain.image.enums.ImageKind;
import global.annotation.Business;
import java.nio.file.Path;
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
    private final FileUtils fileUtils;

    /**
     * DEFAULT_IMAGE 는 하나만 존재 → 있으면 교체, 없으면 새로 저장 <br>
     * DEFAULT_IMAGE 의 서버 파일 이름은 **default.확장자** 로 제한한다. -> storeDefaultImage 에서 이름 지정 <br>
     * TIP_IMAGE 등은 단순 저장
     */
    public String uploadImage(MultipartFile multipartFile, ImageKind imageKind) {
        if (multipartFile.isEmpty()) {
            throw new ImageNotFoundException(ImageErrorCode.IMAGE_NOT_FOUND);
        }

        String originalFilename = FileUtils.getOriginalFileName(multipartFile);
        String extension = FileUtils.getExtension(originalFilename);

        if (!FileUtils.ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            log.error("허용되지 않은 이미지 확장자: {}", extension);
            throw new InvalidImageExtensionException(ImageErrorCode.INVALID_EXTENSION);
        }

        if (imageKind == ImageKind.DEFAULT_IMAGE) {
            return imageService.getImageByKind(ImageKind.DEFAULT_IMAGE).map(existingImage -> {

                if (!existingImage.getExtension().equals(extension)) {
                    imageService.deleteImageMetaData(existingImage);
                    localImageStorageService.deleteImageAsync(existingImage.getServerName(), existingImage.getExtension());
                }

                existingImage.setOriginalName(FileUtils.getFileOfName(originalFilename));
                existingImage.setExtension(extension);
                existingImage.setImageUrl(fileUtils.createImageUrl(Path.of(localImageStorageService.DEFAULT_IMAGE_NAME + extension)));

                imageService.saveImageMetaDataAsync(existingImage);
                localImageStorageService.storeDefaultImage(multipartFile);
                return existingImage.getImageUrl();
            }).orElseGet(() -> {

                Path savedPath = localImageStorageService.storeDefaultImage(multipartFile);

                ImageDocument document = imageConverter.toDocument(multipartFile,
                    ImageKind.DEFAULT_IMAGE, savedPath);
                imageService.saveImageMetaDataAsync(document);
                return document.getImageUrl();
            });
        }

        Path savedPath = localImageStorageService.storeImage(multipartFile);
        ImageDocument document = imageConverter.toDocument(multipartFile, imageKind, savedPath);
        imageService.saveImageMetaDataAsync(document);
        return document.getImageUrl();
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
