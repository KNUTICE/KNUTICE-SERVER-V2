package api.domain.image.service;

import db.domain.image.ImageDocument;
import db.domain.image.ImageMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageMongoRepository imageMongoRepository;

    public void saveImageMetaData(ImageDocument imageDocument) {
        imageMongoRepository.save(imageDocument);
    }

}
