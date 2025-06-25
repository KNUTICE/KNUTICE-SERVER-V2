package api.domain.image.controller.model;

import db.domain.image.enums.ImageKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {

    private String imageId;

    private String imageUrl;

    private ImageKind imageKind;

}
