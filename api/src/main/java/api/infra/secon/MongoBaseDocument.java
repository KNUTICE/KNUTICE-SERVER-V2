package api.infra.secon;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@SuperBuilder
@NoArgsConstructor
public class MongoBaseDocument {

    @CreatedDate
    @Builder.Default
    protected LocalDateTime createdAt = null; // @Id 값을 미리 생성한 경우 createdAt 생성 불가

    @LastModifiedDate
    @Builder.Default
    protected LocalDateTime updatedAt = null;

}
