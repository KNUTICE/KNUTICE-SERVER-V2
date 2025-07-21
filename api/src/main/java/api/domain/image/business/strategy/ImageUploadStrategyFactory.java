package api.domain.image.business.strategy;

import db.domain.image.enums.ImageKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageUploadStrategyFactory {

    private final DefaultImageUploadStrategy defaultImageUploadStrategy;
    private final TipImageUploadStrategy tipImageUploadStrategy;

    public AbstractImageUploadStrategy getStrategy(ImageKind imageKind) {
        if (imageKind == ImageKind.DEFAULT_IMAGE) {
            return defaultImageUploadStrategy;
        }
        return tipImageUploadStrategy;
    }

}
