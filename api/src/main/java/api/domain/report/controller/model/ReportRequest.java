package api.domain.report.controller.model;

import global.utils.DeviceType;
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
    private String content;

    @NotNull
    private DeviceType clientType;

    @Size(max = 50)
    private String deviceName;

    @Size(max = 50)
    private String version;

}
