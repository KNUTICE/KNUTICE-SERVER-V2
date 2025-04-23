package api.common.feign.request;

import global.utils.NoticeMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmRequest {

    @NotBlank
    private String fcmToken;

    @NotNull
    private Long nttId;

    @NotBlank
    private String title; // 일반소식, 학사공지 등등...

    @NotBlank
    private String content; // 제목내용

    @NotNull
    private NoticeMapper noticeName;

    @NotBlank
    private String contentUrl;

    private String contentImage;

    @NotBlank
    private String departmentName;

    @NotBlank
    private String registeredAt;

}
