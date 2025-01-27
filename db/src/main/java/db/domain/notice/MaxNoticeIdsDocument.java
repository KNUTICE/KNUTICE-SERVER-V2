package db.domain.notice;

import global.utils.NoticeMapper;
import java.time.LocalDateTime;
import jdk.jfr.Description;
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
@Document(collection = "max_notice_ids")
public class MaxNoticeIdsDocument {

    @Id
    private NoticeMapper noticeMapper;

    private Long announcementMaxNttId;

    private Long noticeMaxNttId;

    @Description("[공지] 최대 nttId 등록일 (변경일)")
    private LocalDateTime announcementRegisteredAt;

    @Description("게시글 최대 nttId 등록일 (변경일)")
    private LocalDateTime noticeRegisteredAt;

}
