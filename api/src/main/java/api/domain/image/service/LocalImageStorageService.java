package api.domain.image.service;

import api.common.error.ImageErrorCode;
import api.common.exception.image.ImageStorageDirectoryCreationException;
import api.common.exception.image.ImageStorageWriteException;
import api.domain.image.utils.FileUtils;
import db.domain.image.ImageDocument;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class LocalImageStorageService {

    @Value("${file.upload-dir}")
    private String directoryPath;

    public static final String DEFAULT_IMAGE_NAME = "default";

    public Path storeDefaultImage(MultipartFile file) {
        createDirectoryIfNotExists(directoryPath);
        String extension = FileUtils.getExtension(FileUtils.getOriginalFileName(file));
        Path imagePath = Paths.get(directoryPath, DEFAULT_IMAGE_NAME + extension);
        storeImage(file, imagePath);
        return imagePath;
    }

    public Path storeImage(MultipartFile file) {
        createDirectoryIfNotExists(directoryPath);
        Path imagePath = createImagePath(file.getOriginalFilename());
        storeImage(file, imagePath);
        return imagePath;
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

    @Async
    public void deleteImageAsync(String serverName, String extension) {
        try {
            Path imagePath = Paths.get(directoryPath, serverName + extension);
            Files.deleteIfExists(imagePath);
            log.info("이미지 삭제 성공: {}", imagePath);
        } catch (IOException e) {
            log.error("기존 이미지 파일 삭제 실패: {}", serverName + extension, e);
        }
    }

    private void storeImage(MultipartFile file, Path imagePath) {
        try {
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
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

        String serverName = UUID.randomUUID().toString();
        return Paths.get(directoryPath, serverName + extension);
    }

}
