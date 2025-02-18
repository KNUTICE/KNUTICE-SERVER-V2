package crawler.fcmutils;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import crawler.service.model.FcmDto;
import db.domain.token.fcm.FcmTokenDocument;
import db.domain.token.fcm.FcmTokenMongoRepository;
import global.utils.NoticeMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmUtils {

    private final FcmTokenMongoRepository fcmTokenMongoRepository;

    private Message createMessageBuilder(FcmDto dto, String token) {
        return Message.builder()
            .setToken(token)
//            .setTopic(topic)
            .setNotification(
                Notification.builder()
                    .setTitle(dto.getTitle())
                    .setBody(dto.getContent())
//                        .setImage(dto.getContentImage())
                    .build()
            )
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setNotification(
                        AndroidNotification.builder()
                            .setSound("default")
                            .build()
                    )
                    .build()
            )
            .setApnsConfig(
                ApnsConfig.builder()
                    .setAps(Aps.builder()
                        .setAlert(ApsAlert.builder()
//                                .setLaunchImage(dto.getContentImage())
                            .build())
                        .setSound("default")
                        .build())
                    .build()
            )
            .build();
    }

    private List<Message> createMessageBuilderList(FcmDto dto, List<String> tokenList) {
        return tokenList.stream()
            .map(token -> createMessageBuilder(dto, token))
            .toList();
    }

    public List<MessageWithFcmToken> createMessagesWithTokenList(FcmDto fcmDto, List<String> deviceTokenList) {
        List<MessageWithFcmToken> messageWithTokenList = new ArrayList<>();

        for (String token : deviceTokenList) {
            Message message = createMessageBuilder(fcmDto, token);
            messageWithTokenList.add(new MessageWithFcmToken(message, token));
        }

        return messageWithTokenList;
    }

    public List<FcmTokenDocument> getActivateTopicListBy(NoticeMapper noticeMapper) {
        return switch (noticeMapper) {
            case GENERAL_NEWS -> fcmTokenMongoRepository.findAllByGeneralNewsTopicTrue();
            case SCHOLARSHIP_NEWS -> fcmTokenMongoRepository.findAllByScholarshipNewsTopicTrue();
            case EVENT_NEWS -> fcmTokenMongoRepository.findAllByEventNewsTopicTrue();
            case ACADEMIC_NEWS -> fcmTokenMongoRepository.findAllByAcademicNewsTopicTrue();
        };
    }

    public FcmDto buildMultipleNotice(List<String> noticeTitleList, String category) {
        return FcmDto.builder()
            .title(category)
            .content(noticeTitleList.get(noticeTitleList.size() - 1)
                + "...외 "
                + (noticeTitleList.size() - 1)
                + "개의 공지가 작성되었어요!")
            .build();
    }

    public FcmDto buildSingleNotice(String category, String title) {
        return FcmDto.builder()
            .title(category)
            .content(title)
            .build();
    }

}
