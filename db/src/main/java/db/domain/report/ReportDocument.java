package db.domain.report;

import db.domain.report.enums.ClientType;
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
@Document(collection = "report")
public class ReportDocument {

    @Id
    private String id;

    private String fcmToken;

    private String content;

    private ClientType clientType;

    private String deviceName;

    private String version;

    private LocalDateTime registeredAt;

}
