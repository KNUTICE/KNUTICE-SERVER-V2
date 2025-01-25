package global.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultStatus {

    SUCCESS("성공"),
    FAIL("실패")
    ;

    private String description;
}