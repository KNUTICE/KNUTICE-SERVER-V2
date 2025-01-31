package api.domain.report.controller.model;

import db.domain.report.enums.ClientType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    @NotBlank
    private String fcmToken;

    @Size(min = 5, max = 500)
    @NotBlank
    private String content;

    @NotNull
    private ClientType clientType;

    @NotBlank
    @Size(max = 50)
    private String deviceName;

    @NotBlank
    @Size(max = 50)
    private String version;

}
