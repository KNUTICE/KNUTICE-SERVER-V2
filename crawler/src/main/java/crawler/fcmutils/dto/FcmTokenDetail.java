package crawler.fcmutils.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FcmTokenDetail {

    private String fcmToken;

    private Boolean apnsEnabled;

    private int badgeCount;

}
