package db.domain.urgent;

import java.time.LocalDate;
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
@Document(collection = "urgent_notice")
public class UrgentNoticeDocument {

    @Id
    private String id;

    private String title;

    private String content;

    private String contentUrl;

    private LocalDate registeredAt;

}
