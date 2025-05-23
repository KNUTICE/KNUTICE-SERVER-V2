package db.domain.token.jwt;

import db.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "jwt_token")
public class JwtTokenDocument {

    @Id
    private String userId;

    private UserRole role;

    private String refreshToken;

}
