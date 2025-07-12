package api.domain.image.business;

import api.common.error.ImageErrorCode;
import api.common.exception.image.ImageNotFoundException;
import api.common.exception.image.ImageStorageWriteException;
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

        try {
            if (imageKind == ImageKind.DEFAULT_IMAGE) {
                return handleDefaultImageUpload(multipartFile, originalFilename, extension);
            } else {
                return uploadNewImage(multipartFile, imageKind);
            }
        } catch (Exception e) {
            log.error("이미지 업로드 실패");
            throw new ImageStorageWriteException(ImageErrorCode.IMAGE_STORAGE_FAILED);
        }

    }

    private String handleDefaultImageUpload(MultipartFile multipartFile, String originalFilename,
        String extension) {
        return imageService.getImageByKind(ImageKind.DEFAULT_IMAGE)
            .map(existingImage ->
                replaceExistingDefaultImage(multipartFile, existingImage, originalFilename, extension))
            .orElseGet(() ->
                uploadNewImage(multipartFile, ImageKind.DEFAULT_IMAGE)
            );
    }

    private String replaceExistingDefaultImage(MultipartFile multipartFile,
        ImageDocument existingImage, String originalFilename, String extension) {
        String replaceFilename = existingImage.getServerName() + extension;

        if (!existingImage.getExtension().equals(extension)) {
            localImageStorageService.deleteImageAsync(existingImage);
        }

        // 파일 저장
        localImageStorageService.replaceImageAsync(multipartFile, replaceFilename).join();

        // 메타데이터 갱신
        existingImage.setOriginalName(FileUtils.getFileOfName(originalFilename));
        existingImage.setExtension(extension);
        existingImage.setImageUrl(fileUtils.createImageUrl(Path.of(replaceFilename)));

        imageService.saveImageMetaDataAsync(existingImage);
        return existingImage.getImageUrl();
    }

    private String uploadNewImage(MultipartFile multipartFile, ImageKind imageKind) {
        Path savedPath = localImageStorageService.storeImageAsync(multipartFile).join();
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
            imageService.deleteImageBy(imageDocument.getId());
        }, () -> {
            throw new ImageNotFoundException(ImageErrorCode.IMAGE_NOT_FOUND);
        });
    }

}
