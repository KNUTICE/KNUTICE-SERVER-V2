package crawler.fcmutils;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import crawler.fcmutils.dto.MessageWithFcmToken;
import crawler.service.model.FcmDto;
import crawler.worker.model.NoticeDto;
import db.domain.token.fcm.FcmTokenDocument;
import db.domain.token.fcm.FcmTokenMongoRepository;
import global.utils.NoticeMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
                    .setTitle(dto.getTitle()) // 일반공지, 학사공지 등
                    .setBody(dto.getContent())
                    .setImage(dto.getContentImage())
                    .build()
            )
            .putData("nttId", String.valueOf(dto.getNttId()))
            .putData("contentTitle", dto.getContent())
            .putData("contentUrl",dto.getContentUrl())
            .putData("contentImage", dto.getContentImage())
            .putData("departmentName", dto.getDepartmentName())
            .putData("registeredAt", dto.getRegisteredAt())
            .putData("noticeName", dto.getNoticeName().getCategory())
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
                            .setLaunchImage(dto.getContentImage())
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

    public FcmDto buildMultipleNotice(List<NoticeDto> noticeDtoList) {
        return FcmDto.builder()
            .nttId(noticeDtoList.get(0).getNttId())
            .title(noticeDtoList.get(0).getNoticeMapper().getCategory())
            .content(noticeDtoList.get(noticeDtoList.size() - 1).getTitle()
                + "...외 "
                + (noticeDtoList.size() - 1)
                + "개의 공지가 작성되었어요!")
            .noticeName(noticeDtoList.get(0).getNoticeMapper())
            .contentUrl(noticeDtoList.get(0).getContentUrl())
            .contentImage(noticeDtoList.get(0).getContentImage())
            .departmentName(noticeDtoList.get(0).getDepartName())
            .registeredAt(noticeDtoList.get(0).getRegisteredAt())
            .build();
    }

    public FcmDto buildSingleNotice(NoticeDto noticeDto) {
        return FcmDto.builder()
            .nttId(noticeDto.getNttId())
            .title(noticeDto.getNoticeMapper().getCategory())
            .content(noticeDto.getTitle())
            .noticeName(noticeDto.getNoticeMapper())
            .contentUrl(noticeDto.getContentUrl())
            .contentImage(noticeDto.getContentImage())
            .departmentName(noticeDto.getDepartName())
            .registeredAt(noticeDto.getRegisteredAt())
            .build();
    }

    @Transactional
    public void manageToken(List<String> tokenListToDelete, List<String> tokenListToUpdate) {

        List<FcmTokenDocument> updateTokenList = new ArrayList<>(); // 업데이트 대상

        if (!tokenListToUpdate.isEmpty()) {
            List<FcmTokenDocument> fcmTokenDocumentList = fcmTokenMongoRepository.findAllById(tokenListToUpdate);
            for (FcmTokenDocument fcmTokenDocument : fcmTokenDocumentList) {
                fcmTokenDocument.setFailedCount(fcmTokenDocument.getFailedCount() + 1);

                if (fcmTokenDocument.getFailedCount() > 20) {
                    tokenListToDelete.add(fcmTokenDocument.getFcmToken());
                } else {
                    updateTokenList.add(fcmTokenDocument);
                }
            }
        }

        deleteTokens(tokenListToDelete);
        updateTokens(updateTokenList);

    }

    private void updateTokens(List<FcmTokenDocument> fcmTokenDocumentList) {
        if (!fcmTokenDocumentList.isEmpty()) {
            fcmTokenMongoRepository.saveAll(fcmTokenDocumentList);
            log.info("[ALERT] FailedCount 증가 토큰 개수: {}", fcmTokenDocumentList.size());
        }
    }

    private void deleteTokens(List<String> tokenList) {
        if (!tokenList.isEmpty()) {
            fcmTokenMongoRepository.deleteAllById(tokenList);
            log.info("[ALERT] 삭제된 토큰 개수: {}", tokenList.size());
        }
    }

}
