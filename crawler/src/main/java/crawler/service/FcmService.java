package crawler.service;

import com.google.api.core.ApiFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.SendResponse;
import crawler.fcmutils.FcmUtils;
import crawler.service.model.FcmDto;
import db.domain.token.DeviceTokenDocument;
import db.domain.token.DeviceTokenMongoRepository;
import global.errorcode.ErrorCode;
import global.utils.NoticeMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private final DeviceTokenMongoRepository deviceTokenMongoRepository;
    private final TokenManager tokenManager;

    private final FcmUtils fcmUtils;
    private final int MAX_DEVICES_PER_MESSAGE = 500;

    public void sendToTopic(FcmDto dto, NoticeMapper noticeMapper) {

        // NoticeMapper에 따른 활성화된 디바이트 토큰 조회
        List<DeviceTokenDocument> deviceTokenDocumentList = this.getActivateTopicListBy(noticeMapper);
        log.info("전송 기기 토큰 개수 : {}", deviceTokenDocumentList.size());

        List<String> deviceTokenList = deviceTokenDocumentList.stream()
            .map(DeviceTokenDocument::getToken)
            .toList();

        log.info("전송 기기 토큰 : {}", deviceTokenList);

        // 500 개씩 나눠서 전송
        for (int i = 0; i < deviceTokenList.size(); i += MAX_DEVICES_PER_MESSAGE) {

            List<String> tokenList = deviceTokenList.subList(i,
                Math.min(i + MAX_DEVICES_PER_MESSAGE, deviceTokenList.size()));

            // Message List 생성
            List<Message> messageList = fcmUtils.createMessageBuilderList(dto, tokenList);

            // 주제 발송
            ApiFuture<BatchResponse> batchResponseApiFuture = FirebaseMessaging.getInstance().sendEachAsync(messageList);

            batchResponseApiFuture.addListener(() -> {
                try {
                    BatchResponse batchResponse = batchResponseApiFuture.get(); // Blocking
                    if (batchResponse.getFailureCount() > 0) {
                        List<SendResponse> responses = batchResponse.getResponses();
                        List<String> failedTokenList = new ArrayList<>();
                        for (int k = 0; k < responses.size(); k++) {
                            if (!responses.get(k).isSuccessful()) {
                                failedTokenList.add(tokenList.get(k));
                            }
                        }
                        handleFailedTokenList(failedTokenList, dto);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    log.error("비동기 전송 중 오류 : {}", e.getMessage());
                    throw new RuntimeException("비동기 처리 중 오류 발생", e);
                }
            }, MoreExecutors.directExecutor());
        }
    }

    public void fcmTrigger(List<String> noticeTitleList, NoticeMapper noticeMapper)
        throws FirebaseMessagingException {
        String category = noticeMapper.getCategory();
        StringBuilder contentBuilder = new StringBuilder();

        // 로그로 출력
        for (String title : noticeTitleList) {
            log.info("new! - {} : {}", category, title);
        }

        // 공지사항이 3개 이상일 때
        int noticeListSize = noticeTitleList.size();
        if (noticeListSize >= 3) {
            contentBuilder
                .append(noticeTitleList.get(noticeListSize - 1))
                .append("...외 ")
                .append(noticeListSize - 1)
                .append("개의 공지가 작성되었어요!");

            FcmDto fcmDto = FcmDto.builder()
                .title(category)
                .content(contentBuilder.toString())
                .build();
            this.sendToTopic(fcmDto, noticeMapper);
        } else {
            // 3개 미만일 때는 각각 전송
            for (String title : noticeTitleList) {
                FcmDto fcmDto = FcmDto.builder()
                    .title(category)
                    .content(title)
                    .build();
                this.sendToTopic(fcmDto, noticeMapper);
            }
        }
    }

    private List<DeviceTokenDocument> getActivateTopicListBy(NoticeMapper noticeMapper) {
        switch (noticeMapper) {
            case GENERAL_NEWS:
                return deviceTokenMongoRepository.findAllByGeneralNewsTopicTrue();
            case SCHOLARSHIP_NEWS:
                return deviceTokenMongoRepository.findAllByScholarshipNewsTopicTrue();
            case EVENT_NEWS:
                return deviceTokenMongoRepository.findAllByEventNewsTopicTrue();
            case ACADEMIC_NEWS:
                return deviceTokenMongoRepository.findAllByAcademicNewsTopicTrue();
            default:
                throw new RuntimeException(ErrorCode.BAD_REQUEST.getDescription());
        }
    }

    private void handleFailedTokenList(List<String> failedTokenList, FcmDto dto) {
        if (failedTokenList.isEmpty()) {
            return;
        }
        tokenManager.processFailedTokens(failedTokenList, dto);
    }
}