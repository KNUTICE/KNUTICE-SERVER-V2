package api.domain.fcm.controller.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FcmTokenUpdateRequest {

    @NotBlank
    private String oldFcmToken;

    @NotBlank
    private String newFcmToken;

}
