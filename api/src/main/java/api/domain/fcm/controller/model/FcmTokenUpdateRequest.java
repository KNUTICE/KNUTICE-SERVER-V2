package api.domain.fcm.controller.model;

import global.utils.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class FcmTokenUpdateRequest {

    // Nullable
    private String oldFcmToken;

    @NotBlank
    private String newFcmToken;

    @NotNull
    private DeviceType deviceType;

}
