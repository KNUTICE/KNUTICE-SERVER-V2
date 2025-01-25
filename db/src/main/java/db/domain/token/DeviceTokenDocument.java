package db.domain.token;

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
@Document(collection = "device_token")
public class DeviceTokenDocument {

    @Id
    private String token;

    @Builder.Default
    private boolean generalNewsTopic = true;

    @Builder.Default
    private boolean scholarshipNewsTopic = true;

    @Builder.Default
    private boolean eventNewsTopic = true;

    @Builder.Default
    private boolean academicNewsTopic = true;

    private LocalDateTime registeredAt;

    @Builder.Default
    private int failedCount = 0;

}
