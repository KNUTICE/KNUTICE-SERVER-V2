package api.domain.tip.controller.model;

import global.utils.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TipSaveRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String url;

    @NotNull
    private DeviceType deviceType;

}
