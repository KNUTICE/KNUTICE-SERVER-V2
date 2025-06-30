package global.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceType {

    iOS("iOS"),
    AOS("Android")
    ;

    private final String description;

}
