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
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FcmUtils {

    private final FcmTokenMongoRepository fcmTokenMongoRepository;

    public FcmUtils(FcmTokenMongoRepository fcmTokenMongoRepository) {
        this.fcmTokenMongoRepository = fcmTokenMongoRepository;
    }

    public Message createMessageBuilder(FcmDto dto, String token) {
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

    public List<Message> createMessageBuilderList(FcmDto dto, List<String> tokenList) {
        return tokenList.stream()
            .map(token -> createMessageBuilder(dto, token))
            .toList();
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

    /**
     * 재전송 실패한 Message 들만 필터링
     */
    public List<MessageWithFcmToken> filterFailedMessages(List<MessageWithFcmToken> messageList,
        BatchResponse batchResponse) {

        List<MessageWithFcmToken> failedMessages = new ArrayList<>();
        List<String> deleteList = new ArrayList<>(); // 삭제 대상

        for (int i = 0; i < batchResponse.getResponses().size(); i++) {

            FirebaseMessagingException exception = batchResponse.getResponses().get(i).getException();
            if (exception != null) {
                MessagingErrorCode messagingErrorCode = exception.getMessagingErrorCode();
                log.warn("Messaging Error Code : {}, TOKEN : {}", messagingErrorCode, messageList.get(i).getFcmToken());

                if ((messagingErrorCode == MessagingErrorCode.UNREGISTERED) || (messagingErrorCode == MessagingErrorCode.INVALID_ARGUMENT)) {
                    deleteList.add(messageList.get(i).getFcmToken());
                } else {
                    failedMessages.add(messageList.get(i));
                }

            }
        }

        if (!deleteList.isEmpty()) {
            fcmTokenMongoRepository.deleteAllByFcmTokenIn(deleteList);
            log.warn("UNREGISTERED, INVALID_ARGUMENT 으로 삭제되는 토큰 개수 : {}", deleteList.size());
        }

        return failedMessages;
    }

    public void updateFailedToken(List<String> failedTokenList) {
        List<FcmTokenDocument> tokenDocumentList = fcmTokenMongoRepository.findAllById(failedTokenList);

        List<FcmTokenDocument> deleteList = new ArrayList<>(); // 삭제 대상
        List<FcmTokenDocument> updateList = new ArrayList<>(); // 업데이트 대상

        for (FcmTokenDocument tokenDocument : tokenDocumentList) {
            tokenDocument.setFailedCount(tokenDocument.getFailedCount() + 1);

            if (tokenDocument.getFailedCount() > 20) {
                deleteList.add(tokenDocument);
            } else {
                updateList.add(tokenDocument);
            }
        }

        deleteTokens(deleteList);
        updateTokens(updateList);
    }

    private void updateTokens(List<FcmTokenDocument> updateList) {
        if (!updateList.isEmpty()) {
            fcmTokenMongoRepository.saveAll(updateList);
            log.info("FailedCount + 1 토큰 개수: {}", updateList.size());
        }
    }

    private void deleteTokens(List<FcmTokenDocument> deleteList) {
        if (!deleteList.isEmpty()) {
            fcmTokenMongoRepository.deleteAll(deleteList);
            log.info("삭제된 토큰 개수: {}", deleteList.size());
        }
    }

}
