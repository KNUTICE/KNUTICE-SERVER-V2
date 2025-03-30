package api.domain.fcm.controller.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenRequest {

    @NotBlank
    private String fcmToken;

    private Boolean apnsEnabled; // iOS 체크

}
