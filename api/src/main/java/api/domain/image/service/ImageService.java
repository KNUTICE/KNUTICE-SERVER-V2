package api.domain.image.service;

import db.domain.image.ImageDocument;
import db.domain.image.ImageMongoRepository;
import db.domain.image.enums.ImageKind;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageMongoRepository imageMongoRepository;

    public void saveImageMetaData(ImageDocument imageDocument) {
        imageMongoRepository.save(imageDocument);
    }

    public Optional<ImageDocument> getImageByKind(ImageKind imageKind) {
        return imageMongoRepository.findFirstByImageKind(imageKind);
    }

    public void deleteImageMetaData(ImageDocument imageDocument) {
            imageMongoRepository.delete(imageDocument);
    }

    public List<ImageDocument> getImagesBy(ImageKind imageKind) {
        return imageMongoRepository.findAllByImageKind(imageKind);
    }

}
