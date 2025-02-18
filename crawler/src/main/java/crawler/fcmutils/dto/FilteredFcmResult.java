package crawler.fcmutils.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FilteredFcmResult {

    private final List<MessageWithFcmToken> failedMessageList;

    private final List<String> deleteTokenList;

}
