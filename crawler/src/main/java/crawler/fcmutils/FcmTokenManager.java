package crawler.fcmutils;

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
public class FcmTokenManager {

    private final FcmTokenMongoRepository fcmTokenMongoRepository;

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


    public List<FcmTokenDocument> getActivateTopicListBy(NoticeMapper noticeMapper) {
        return switch (noticeMapper) {
            case GENERAL_NEWS -> fcmTokenMongoRepository.findAllByGeneralNewsTopicTrue();
            case SCHOLARSHIP_NEWS -> fcmTokenMongoRepository.findAllByScholarshipNewsTopicTrue();
            case EVENT_NEWS -> fcmTokenMongoRepository.findAllByEventNewsTopicTrue();
            case ACADEMIC_NEWS -> fcmTokenMongoRepository.findAllByAcademicNewsTopicTrue();
            case EMPLOYMENT_NEWS -> fcmTokenMongoRepository.findAllByEmploymentNewsTopic();
        };
    }

}
