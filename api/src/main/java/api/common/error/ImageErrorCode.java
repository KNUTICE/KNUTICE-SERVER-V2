package api.common.error;

import global.errorcode.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageErrorCode implements ErrorCodeIfs {

    IMAGE_NOT_FOUND(400, 5300, "이미지가 존재하지 않습니다."),
    INVALID_EXTENSION(400, 5301, "허용되지 않은 이미지 확장자입니다."),
    DIRECTORY_CREATION_FAILED(500, 5302, "디렉터리를 생성할 수 없습니다."),
    IMAGE_STORAGE_FAILED(500, 5303, "이미지를 저장하는 데 실패했습니다.")
    ;

    private final Integer httpCode;
    private final Integer errorCode;
    private final String description;

}
