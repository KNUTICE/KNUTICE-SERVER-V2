package crawler.service.model;

import global.utils.NoticeMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmDto {

    private String title;

    private String content;

    private NoticeMapper noticeName;

    private String contentUrl;

}
