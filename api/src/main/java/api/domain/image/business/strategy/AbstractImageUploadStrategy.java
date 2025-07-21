package api.domain.image.business.strategy;

import api.common.error.ImageErrorCode;
import api.common.exception.image.ImageNotFoundException;
import api.common.exception.image.InvalidImageExtensionException;
import api.domain.image.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public abstract class AbstractImageUploadStrategy {

    public String upload(MultipartFile multipartFile) {
        validate(multipartFile);

        String originalFilename = FileUtils.getOriginalFileName(multipartFile);
        String extension = FileUtils.getExtension(originalFilename);

        if (!FileUtils.ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            log.error("허용되지 않은 이미지 확장자: {}", extension);
            throw new InvalidImageExtensionException(ImageErrorCode.INVALID_EXTENSION);
        }

        return doUpload(multipartFile, originalFilename, extension);
    }

    private void validate(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new ImageNotFoundException(ImageErrorCode.IMAGE_NOT_FOUND);
        }
    }

    protected abstract String doUpload(MultipartFile multipartFile, String originalFilename, String extension);

}
