package db.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    REGISTERED("등록"),
    UNREGISTERED("탈퇴"),
    PENDING("승인 대기")
    ;

    private final String description;

}