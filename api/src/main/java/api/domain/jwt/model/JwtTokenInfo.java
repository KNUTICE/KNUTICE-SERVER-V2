package api.domain.jwt.model;

import db.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtTokenInfo {

    private String userId;

    private UserRole role;

}
