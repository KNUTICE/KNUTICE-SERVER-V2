package api.domain.urgent.controller.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UrgentNoticeSaveRequest {

    @NotBlank
    private String title;

    @Size(min = 1, max = 255)
    private String content;

    private String contentUrl;

}
