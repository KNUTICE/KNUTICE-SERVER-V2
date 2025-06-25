package db.domain.image;

import db.domain.image.enums.ImageKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "image")
public class ImageDocument {

    @Id
    private String id;

    private String imageUrl;

    private String originalName;

    private String serverName;

    private String extension;

    private ImageKind imageKind;

}
