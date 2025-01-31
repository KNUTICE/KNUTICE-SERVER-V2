package db.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

    ADMIN("관리자"),
    BASIC_USER("일반사용자"),
    ;

    private final String description;

}
