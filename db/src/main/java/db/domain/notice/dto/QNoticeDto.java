package db.domain.notice.dto;

import global.utils.NoticeMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QNoticeDto {

    private NoticeMapper noticeName;

    private Long nttId;

    private Pageable page;

}
