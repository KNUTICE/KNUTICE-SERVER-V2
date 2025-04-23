package db.domain.notice;

import global.utils.NoticeMapper;
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
@Document(collection = "notice")
public class NoticeDocument {

    @Id
    private Long nttId;

    private NoticeMapper noticeName;

    private String title;

    private Integer contentNumber;

    private String contentUrl;

    private String contentImage;

    private String departmentName;

    private String registeredAt;

}
