package api.domain.image.service;

import api.common.error.ImageErrorCode;
import api.common.exception.image.ImageStorageDirectoryCreationException;
import api.common.exception.image.ImageStorageWriteException;
import api.common.exception.image.InvalidImageExtensionException;
import api.domain.image.utils.FileUtils;
import db.domain.image.ImageDocument;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class LocalImageStorageService {

    @Value("${file.upload-dir}")
    private String directoryPath;

    @Async
    public CompletableFuture<Path> storeImageAsync(MultipartFile multipartFile) {
        createDirectoryIfNotExists(directoryPath);
        Path imagePath = createImagePath(multipartFile);
        try {
            Files.copy(multipartFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("", e);
            log.error("이미지를 저장하는 데 실패했습니다.");
            throw new ImageStorageWriteException(ImageErrorCode.IMAGE_STORAGE_FAILED);
        }
        return CompletableFuture.completedFuture(imagePath);
    }

    @Async
    public void deleteImageAsync(ImageDocument imageDocument) {
        try {
            Path imagePath = Paths.get(directoryPath, imageDocument.getServerName() + imageDocument.getExtension());
            Files.deleteIfExists(imagePath);
            log.info("이미지 삭제 성공");
        } catch (IOException e) {
            log.error("기존 이미지 파일 삭제 실패: {}", imageDocument.getImageUrl(), e);
        }
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            log.error("디렉터리를 생성할 수 없습니다.");
            throw new ImageStorageDirectoryCreationException(ImageErrorCode.DIRECTORY_CREATION_FAILED);
        }
    }

    private Path createImagePath(MultipartFile multipartFile) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String extension = FileUtils.getExtension(fileName);

        if (!FileUtils.ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            log.error("허용되지 않은 이미지 확장자입니다.");
            throw new InvalidImageExtensionException(ImageErrorCode.INVALID_EXTENSION);
        }

        String serverName = UUID.randomUUID().toString();
        return Paths.get(directoryPath, serverName + extension);
    }

}
