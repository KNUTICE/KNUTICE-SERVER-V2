package db.domain.user;

import db.domain.user.enums.UserRole;
import db.domain.user.enums.UserStatus;
import java.time.LocalDateTime;
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
@Document(collection = "user")
public class UserDocument {

    @Id
    private String id;

    private String name;

    private UserRole role;

    private UserStatus status;

    private LocalDateTime registeredAt;

    private LocalDateTime lastLoginAt;

    private Account account;

}
