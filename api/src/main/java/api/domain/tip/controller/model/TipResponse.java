package api.domain.tip.controller.model;

import global.utils.DeviceType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipResponse {

    private String id;

    private String title;

    private String url;

    private DeviceType deviceType;

    private LocalDateTime registeredAt;

}
