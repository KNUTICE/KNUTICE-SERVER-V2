package api.domain.image.converter;

import api.domain.image.controller.model.ImageResponse;
import db.domain.image.ImageDocument;
import global.annotation.Converter;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class ImageConverter {

    public List<ImageResponse> toResponse(List<ImageDocument> imageDocumentList) {
        return imageDocumentList.stream()
            .map(imageDocument -> ImageResponse.builder()
                .imageId(imageDocument.getId())
                .imageUrl(imageDocument.getImageUrl())
                .imageKind(imageDocument.getImageKind())
                .build())
            .collect(Collectors.toList());
    }

}
