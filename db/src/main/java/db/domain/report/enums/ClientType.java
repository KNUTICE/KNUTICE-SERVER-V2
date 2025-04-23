package db.domain.report.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClientType {

    WEB("웹"),
    APP("앱")
    ;

    private String description;

}
