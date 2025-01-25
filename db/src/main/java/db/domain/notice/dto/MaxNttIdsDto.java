package db.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MaxNttIdsDto {
    private Long announcementMaxNttId;
    private Long noticeMaxNttId;
}
