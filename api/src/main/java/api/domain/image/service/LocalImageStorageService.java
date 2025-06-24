package api.domain.image.service;

import api.common.error.ImageErrorCode;
import api.common.exception.image.ImageStorageDirectoryCreationException;
import api.common.exception.image.ImageStorageWriteException;
import api.common.exception.image.InvalidImageExtensionException;
import api.domain.image.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalImageStorageService {

    @Value("${file.upload-dir}")
    private String directoryPath;

    public Path storeImage(MultipartFile multipartFile) {

        createDirectoryIfNotExists(directoryPath);
        Path imagePath = createImagePath(multipartFile);

        try {
            Files.copy(multipartFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ImageStorageWriteException(ImageErrorCode.IMAGE_STORAGE_FAILED);
        }

        return imagePath;

    }

    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new ImageStorageDirectoryCreationException(ImageErrorCode.DIRECTORY_CREATION_FAILED);
        }
    }

    private Path createImagePath(MultipartFile multipartFile) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String extension = FileUtils.getExtension(fileName);

        if (!FileUtils.ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new InvalidImageExtensionException(ImageErrorCode.INVALID_EXTENSION);
        }

        String serverName = UUID.randomUUID().toString();
        return Paths.get(directoryPath, serverName + extension);
    }

}
