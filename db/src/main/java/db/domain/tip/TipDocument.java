package db.domain.tip;

import global.utils.DeviceType;
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
@Document(collection = "tip")
public class TipDocument {

    @Id
    private String id;

    private String title;

    private String url;

    private DeviceType deviceType;

    private LocalDateTime registeredAt;

}
