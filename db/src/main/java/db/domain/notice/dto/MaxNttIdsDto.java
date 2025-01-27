package db.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaxNttIdsDto {
    private Long announcementMaxNttId;
    private Long noticeMaxNttId;
}
