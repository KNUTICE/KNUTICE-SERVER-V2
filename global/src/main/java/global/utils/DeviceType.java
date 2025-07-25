package global.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceType {

    iOS("iOS"),
    AOS("Android"),

    WEB("웹"),
    APP("앱")
    ;

    private final String description;

}
