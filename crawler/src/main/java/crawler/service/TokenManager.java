package crawler.service;

import com.google.api.core.ApiFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.SendResponse;
import crawler.fcmutils.FcmUtils;
import crawler.service.model.FcmDto;
import db.domain.token.DeviceTokenDocument;
import db.domain.token.DeviceTokenMongoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenManager {

    private final DeviceTokenMongoRepository deviceTokenMongoRepository;
    private final FcmUtils fcmUtils;

    /**
     * 실패한 토큰 관리
     * 실패 횟수가 20회를 초과하면 삭제하고, 그렇지 않으면 실패 횟수를 업데이트.
     *
     *
     * Author : SEOB
     * */
    @Transactional
    public void processFailedTokens(List<String> failedTokenList, FcmDto dto) {
        log.warn("실패한 내용 : {}", dto.getContent());
        log.info("실패 토큰 개수: {}", failedTokenList.size());

        try {
            List<String> reFailedTokenList = reSendMessageAsync(failedTokenList, dto).get();

            List<DeviceTokenDocument> tokenDocumentList = deviceTokenMongoRepository.findAllById(reFailedTokenList);

            List<DeviceTokenDocument> deleteList = new ArrayList<>(); // 삭제 대상
            List<DeviceTokenDocument> updateList = new ArrayList<>(); // 업데이트 대상

            for (DeviceTokenDocument tokenDocument : tokenDocumentList) {
                tokenDocument.setFailedCount(tokenDocument.getFailedCount() + 1);

                if (tokenDocument.getFailedCount() > 20) {
                    deleteList.add(tokenDocument);
                } else {
                    updateList.add(tokenDocument);
                }
            }

            deleteTokens(deleteList);
            updateTokens(updateList);
        } catch (InterruptedException | ExecutionException e) {
            log.error("재전송 작업 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("재전송 처리 실패", e);
        }
    }

    private void updateTokens(List<DeviceTokenDocument> updateList) {
        if (!updateList.isEmpty()) {
            deviceTokenMongoRepository.saveAll(updateList);
            log.info("업데이트된 토큰 개수: {}", updateList.size());
        }
    }

    private void deleteTokens(List<DeviceTokenDocument> deleteList) {
        if (!deleteList.isEmpty()) {
            deviceTokenMongoRepository.deleteAll(deleteList);
            log.info("삭제된 토큰 개수: {}", deleteList.size());
        }
    }

    /**
     * 실패토큰을 다시 재전송합니다.
     */
    private CompletableFuture<List<String>> reSendMessageAsync(List<String> failedTokenList, FcmDto dto) {
        List<Message> messageList = fcmUtils.createMessageBuilderList(dto, failedTokenList);

        CompletableFuture<List<String>> future = new CompletableFuture<>();

        ApiFuture<BatchResponse> batchResponseApiFuture = FirebaseMessaging.getInstance().sendEachAsync(messageList);

        batchResponseApiFuture.addListener(() -> {
            try {
                BatchResponse batchResponse = batchResponseApiFuture.get(); // Blocking
                List<String> reFailedTokenList = new ArrayList<>();
                if (batchResponse.getFailureCount() > 0) {
                    List<SendResponse> responses = batchResponse.getResponses();
                    for (int k = 0; k < responses.size(); k++) {
                        if (!responses.get(k).isSuccessful()) {
                            reFailedTokenList.add(failedTokenList.get(k));
                        }
                    }
                }
                future.complete(reFailedTokenList); // 성공 시 결과 반환
            } catch (Exception e) {
                log.error("비동기 전송 중 오류 : {}", e.getMessage());
                future.completeExceptionally(e);
            }
        }, MoreExecutors.directExecutor());

        return future;
    }

}
