package api.domain.topic.controller.model;

import global.utils.NoticeMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicSubscriptionRequest {

    @NotBlank
    private String deviceToken;

    @NotNull
    private NoticeMapper noticeName;

    @NotNull
    private Boolean isSubscribed;

}
