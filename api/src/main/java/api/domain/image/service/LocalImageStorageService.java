package api.domain.image.service;

import api.common.error.ImageErrorCode;
import api.common.exception.image.ImageStorageDirectoryCreationException;
import api.common.exception.image.ImageStorageWriteException;
import api.common.exception.image.InvalidImageExtensionException;
import api.domain.image.utils.FileUtils;
import db.domain.image.ImageDocument;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LocalImageStorageService {

    @Value("${file.upload-dir}")
    private String directoryPath;

    @Async
    public CompletableFuture<Path> storeImageAsync(byte[] imageData, String originalFilename) {
        createDirectoryIfNotExists(directoryPath);
        Path imagePath = createImagePath(originalFilename);
        storeImage(imageData, imagePath);
        return CompletableFuture.completedFuture(imagePath);
    }

    @Async
    public CompletableFuture<Path> replaceImageAsync(byte[] imageData, String existingFilename) {
        createDirectoryIfNotExists(directoryPath);
        Path imagePath = Paths.get(directoryPath, existingFilename);
        storeImage(imageData, imagePath);
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

    private void storeImage(byte[] imageData, Path imagePath) {
        try {
            Files.copy(new ByteArrayInputStream(imageData), imagePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("이미지를 저장하는 데 성공했습니다: {}", imagePath);
        } catch (IOException e) {
            log.error("이미지를 저장하는 데 실패했습니다: {}", imagePath, e);
            throw new ImageStorageWriteException(ImageErrorCode.IMAGE_STORAGE_FAILED);
        }
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            log.error("디렉터리를 생성할 수 없습니다.");
            throw new ImageStorageDirectoryCreationException(ImageErrorCode.DIRECTORY_CREATION_FAILED);
        }
    }

    private Path createImagePath(String originalFilename) {
        String extension = FileUtils.getExtension(originalFilename);

        if (!FileUtils.ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            log.error("허용되지 않은 이미지 확장자입니다.");
            throw new InvalidImageExtensionException(ImageErrorCode.INVALID_EXTENSION);
        }

        String serverName = UUID.randomUUID().toString();
        return Paths.get(directoryPath, serverName + extension);
    }

}
