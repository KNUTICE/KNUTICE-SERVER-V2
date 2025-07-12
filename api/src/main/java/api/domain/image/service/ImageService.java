package api.domain.image.service;

import db.domain.image.ImageDocument;
import db.domain.image.ImageMongoRepository;
import db.domain.image.enums.ImageKind;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageMongoRepository imageMongoRepository;

    @Async
    public void saveImageMetaDataAsync(ImageDocument imageDocument) {
        imageMongoRepository.save(imageDocument);
    }

    public Optional<ImageDocument> getImageByKind(ImageKind imageKind) {
        return imageMongoRepository.findFirstByImageKind(imageKind);
    }

    public void deleteImageMetaData(ImageDocument imageDocument) {
            imageMongoRepository.delete(imageDocument);
    }

    public void deleteImageBy(String imageId) {
        imageMongoRepository.deleteById(imageId);
    }

    public List<ImageDocument> getImagesBy(ImageKind imageKind) {
        return imageMongoRepository.findAllByImageKind(imageKind);
    }

    public Optional<ImageDocument> getImageBy(String imageId) {
        return imageMongoRepository.findById(imageId);
    }

}
