package crawler.fcmutils;

import crawler.fcmutils.dto.FcmTokenDetail;
import crawler.fcmutils.dto.MessageWithFcmToken;
import crawler.fcmutils.messagefactory.MessageFactory;
import crawler.service.model.FcmDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmMessageGenerator {

    private final MessageFactory messageFactory;

    public List<MessageWithFcmToken> generateMessageBuilderList(
        FcmDto fcmDto,
        List<FcmTokenDetail> tokenDetailList
    ) {
        return tokenDetailList.stream()
            .map(tokenDetail -> new MessageWithFcmToken(messageFactory.createMessage(fcmDto, tokenDetail), tokenDetail.getFcmToken()))
            .collect(Collectors.toList());
    }

}