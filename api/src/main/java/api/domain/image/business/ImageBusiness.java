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
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Business
@RequiredArgsConstructor
public class ImageBusiness {

    private final LocalImageStorageService localImageStorageService;
    private final ImageService imageService;

    private final ImageConverter imageConverter;

    @Value("${url.host}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Value("${url.scheme}")
    private String scheme;

    @Value("${file.context-path}")
    private String contextPath;

    public void uploadImage(MultipartFile multipartFile, ImageKind imageKind) {
        if (multipartFile.isEmpty()) {
            throw new ImageNotFoundException(ImageErrorCode.IMAGE_NOT_FOUND);
        }

        // DEFAULT IMAGE 가 존재하는 경우 삭제
        if (imageKind.equals(ImageKind.DEFAULT_IMAGE)) {
            imageService.getImageByKind(imageKind).ifPresent(existingImage -> {
                imageService.deleteImageMetaData(existingImage);
                localImageStorageService.deleteImageAsync(existingImage);
            });
        }

        // 디렉터리 저장
        localImageStorageService.storeImageAsync(multipartFile).thenAccept(newImagePath -> {
            ImageDocument newImage = createMetaData(multipartFile, imageKind, newImagePath);
            imageService.saveImageMetaData(newImage);
        });
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

    private ImageDocument createMetaData(MultipartFile multipartFile, ImageKind imageKind,
        Path imagePath) {
        String originalFileName = StringUtils.cleanPath(
            Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String fileName = imagePath.getFileName().toString();

        return ImageDocument.builder()
            .imageUrl(createImageUrl(imagePath))
            .originalName(FileUtils.getFileOfName(originalFileName))
            .serverName(FileUtils.getFileOfName(fileName))
            .extension(FileUtils.getExtension(fileName))
            .imageKind(imageKind)
            .build();

    }

    private String createImageUrl(Path filePath) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
            .scheme(scheme)
            .host(host)
            .path(contextPath + filePath.getFileName());

        // localhost 일 때만 포트 추가
        if ("localhost".equalsIgnoreCase(host)) {
            builder.port(port);
        }

        return builder.toUriString();
    }
}
