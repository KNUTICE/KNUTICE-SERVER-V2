package db.domain.image.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageKind {

    DEFAULT_IMAGE("기본 이미지"),
    TIP_IMAGE("사용 팁 이미지")
    ;

    private final String description;

}
