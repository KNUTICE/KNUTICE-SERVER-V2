package api.domain.image.converter;

import api.domain.image.controller.model.ImageResponse;
import api.domain.image.utils.FileUtils;
import db.domain.image.ImageDocument;
import db.domain.image.enums.ImageKind;
import global.annotation.Converter;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Converter
@RequiredArgsConstructor
public class ImageConverter {

    private final FileUtils fileUtils;

    public List<ImageResponse> toResponse(List<ImageDocument> imageDocumentList) {
        return imageDocumentList.stream()
            .map(imageDocument -> ImageResponse.builder()
                .imageId(imageDocument.getId())
                .imageUrl(imageDocument.getImageUrl())
                .imageKind(imageDocument.getImageKind())
                .build())
            .collect(Collectors.toList());
    }

    public ImageDocument toDocument(MultipartFile file, ImageKind imageKind, Path imagePath) {
        String fileName = imagePath.getFileName().toString();
        return ImageDocument.builder()
            .imageUrl(fileUtils.createImageUrl(imagePath))
            .originalName(FileUtils.getFileOfName(file))
            .serverName(FileUtils.getFileOfName(fileName))
            .extension(FileUtils.getExtension(fileName))
            .imageKind(imageKind)
            .build();
    }

}
