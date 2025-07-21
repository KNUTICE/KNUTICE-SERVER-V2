package api.domain.image.business.strategy;

import api.domain.image.converter.ImageConverter;
import api.domain.image.service.ImageService;
import api.domain.image.service.LocalImageStorageService;
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
public class TipImageUploadStrategy extends AbstractImageUploadStrategy {

    private final LocalImageStorageService localImageStorageService;
    private final ImageConverter imageConverter;
    private final ImageService imageService;

    @Override
    protected String doUpload(MultipartFile multipartFile, String originalFilename, String extension) {
        Path savedPath = localImageStorageService.storeImage(multipartFile);
        ImageDocument document = imageConverter.toDocument(multipartFile, ImageKind.TIP_IMAGE, savedPath);
        imageService.saveImageMetaDataAsync(document);
        return document.getImageUrl();
    }

}
