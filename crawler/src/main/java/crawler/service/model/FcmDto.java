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

    private Long nttId;

    private String title; // 일반소식, 학사공지 등등...

    private String content; // 제목내용

    private NoticeMapper noticeName;

    private String contentUrl;

    private String contentImage;

    private String departmentName;

    private String registeredAt;

}
