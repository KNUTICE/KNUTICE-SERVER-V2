package api.domain.image.business;

import api.common.error.ImageErrorCode;
import api.common.exception.image.ImageNotFoundException;
import api.domain.image.service.ImageService;
import api.domain.image.service.LocalImageStorageService;
import api.domain.image.utils.FileUtils;
import db.domain.image.ImageDocument;
import db.domain.image.enums.ImageKind;
import global.annotation.Business;
import java.nio.file.Path;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@Business
@RequiredArgsConstructor
@Slf4j
public class ImageBusiness {

    private final LocalImageStorageService localImageStorageService;
    private final ImageService imageService;

    @Value("${url.host}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Value("${url.scheme}")
    private String scheme;

    @Value("${file.context-path}")
    private String contextPath;

    @Async
    public void uploadImage(MultipartFile multipartFile, ImageKind imageKind) {

        if (multipartFile.isEmpty()) {
            throw new ImageNotFoundException(ImageErrorCode.IMAGE_NOT_FOUND);
        }

        // 디렉터리 저장
        Path imagePath = localImageStorageService.storeImage(multipartFile);

        // DB 저장
        imageService.saveImageMetaData(createMetaData(multipartFile, imageKind, imagePath));
    }

    private ImageDocument createMetaData(MultipartFile multipartFile, ImageKind imageKind, Path imagePath) {
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
