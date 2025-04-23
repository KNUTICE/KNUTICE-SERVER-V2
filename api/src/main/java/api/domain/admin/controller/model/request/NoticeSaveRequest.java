package api.domain.admin.controller.model.request;

import global.utils.NoticeMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoticeSaveRequest {

    @NotNull
    private Long nttId;

    @NotNull
    private Integer contentNumber;

    @NotBlank
    private String title;

    @NotBlank
    private String contentUrl;

    private String contentImage;

    @NotBlank
    private String departmentName;

    @NotBlank
    private String registeredAt;

    @NotNull
    private NoticeMapper noticeName;

}
