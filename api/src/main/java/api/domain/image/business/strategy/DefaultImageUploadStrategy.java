package api.domain.image.business.strategy;

import api.domain.image.converter.ImageConverter;
import api.domain.image.service.ImageService;
import api.domain.image.service.LocalImageStorageService;
import api.domain.image.utils.FileUtils;
import db.domain.image.ImageDocument;
import db.domain.image.enums.ImageKind;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultImageUploadStrategy extends AbstractImageUploadStrategy {

    private final LocalImageStorageService localImageStorageService;
    private final ImageConverter imageConverter;
    private final ImageService imageService;
    private final FileUtils fileUtils;

    @Override
    protected String doUpload(MultipartFile multipartFile, String originalFilename, String extension) {
        return imageService.getImageByKind(ImageKind.DEFAULT_IMAGE).map(existingImage -> {
            if (!existingImage.getExtension().equals(extension)) {
                imageService.deleteImageMetaData(existingImage);
                localImageStorageService.deleteImageAsync(existingImage.getServerName(), existingImage.getExtension());
            }

            existingImage.setOriginalName(FileUtils.getFileOfName(originalFilename));
            existingImage.setExtension(extension);
            existingImage.setImageUrl(fileUtils.createImageUrl(
                Path.of(LocalImageStorageService.DEFAULT_IMAGE_NAME + extension)));

            imageService.saveImageMetaDataAsync(existingImage);
            localImageStorageService.storeDefaultImage(multipartFile);
            return existingImage.getImageUrl();

        }).orElseGet(() -> {
            Path savedPath = localImageStorageService.storeDefaultImage(multipartFile);
            ImageDocument document = imageConverter.toDocument(multipartFile, ImageKind.DEFAULT_IMAGE, savedPath);
            imageService.saveImageMetaDataAsync(document);
            return document.getImageUrl();
        });
    }

}
