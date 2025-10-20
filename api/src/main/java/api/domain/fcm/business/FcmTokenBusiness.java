package api.domain.fcm.business;

import api.domain.fcm.controller.model.FcmTokenInfo;
import api.domain.fcm.controller.model.FcmTokenRequest;
import api.domain.fcm.controller.model.FcmTokenUpdateRequest;
import api.domain.fcm.converter.FcmTokenConverter;
import api.domain.fcm.service.FcmTokenSeconService;
import api.domain.fcm.service.FcmTokenService;
import api.infra.secon.FcmTokenSeconDocument;
import db.domain.token.fcm.FcmTokenDocument;
import global.annotation.Business;
import global.utils.DeviceType;
import global.utils.NoticeMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Business
@RequiredArgsConstructor
public class FcmTokenBusiness {

    private final FcmTokenService fcmTokenService;
    private final FcmTokenSeconService fcmTokenSeconService;

    private final FcmTokenConverter fcmTokenConverter;

    // SecondaryDB 연결
    public Boolean saveFcmToken(FcmTokenRequest fcmTokenRequest) {
        Optional<FcmTokenSeconDocument> fcmTokenSeconDocument = fcmTokenSeconService.getFcmToken(fcmTokenRequest.getFcmToken());

        if (fcmTokenSeconDocument.isPresent()) {
            FcmTokenSeconDocument fcmTokenSeconDocumentExists = fcmTokenSeconDocument.get();
            fcmTokenSeconDocumentExists.setIsActive(true);
            // deviceType 이 null 인 경우 AOS
            fcmTokenSeconDocumentExists.setDeviceType(fcmTokenRequest.getDeviceType() == null ? DeviceType.AOS : fcmTokenRequest.getDeviceType());
            return fcmTokenSeconService.saveFcmToken(fcmTokenSeconDocumentExists);
            // 저장
        } else {
            FcmTokenSeconDocument newFcmTokenSeconDocument = FcmTokenSeconDocument.builder()
                .createdAt(LocalDateTime.now())
                .fcmToken(fcmTokenRequest.getFcmToken())
                .deviceType(fcmTokenRequest.getDeviceType() == null ? DeviceType.AOS : fcmTokenRequest.getDeviceType())
                .subscribedNoticeTopics(Arrays.stream(NoticeMapper.values()).map(Enum::toString).collect(
                    Collectors.toSet()))
                .build();
            return fcmTokenSeconService.saveFcmToken(newFcmTokenSeconDocument);
        }
    }

    /**
     * oldFcmToken == newFcmToken -> return
     * oldFcmToken != newFcmToken -> topic 교체
     * oldFcmToken == null -> newFcmToken 만 저장
     */
    @Transactional
    public Boolean updateFcmToken(FcmTokenUpdateRequest fcmTokenUpdateRequest) {

        String newToken = fcmTokenUpdateRequest.getNewFcmToken();
        if (fcmTokenUpdateRequest.getOldFcmToken() == null) {
            log.info("oldToken NULL!!!! : [newFcmToken : {}]", fcmTokenUpdateRequest.getNewFcmToken());
            this.saveFcmToken(new FcmTokenRequest(newToken, fcmTokenUpdateRequest.getDeviceType()));
            return true;
        }

        String oldToken = fcmTokenUpdateRequest.getOldFcmToken();

        // 동일 토큰이면 서버 저장
        if (oldToken.equals(newToken)) {
            this.saveFcmToken(new FcmTokenRequest(newToken, fcmTokenUpdateRequest.getDeviceType()));
            return true;
        }

        // oldToken 조회
        var oldTokenOptional = fcmTokenService.getFcmToken(oldToken);
        if (oldTokenOptional.isEmpty()) {
            // oldToken 이 없는 경우 newToken 저장
            return this.saveFcmToken(new FcmTokenRequest(newToken, fcmTokenUpdateRequest.getDeviceType()));
        }

        var oldFcmTokenDocument = oldTokenOptional.get();

        // newToken 존재 확인
        var newTokenDocumentOptional = fcmTokenService.getFcmToken(newToken);

        // newToken 이 존재하지 않으면 저장할 새 Document 생성
        FcmTokenDocument newFcmTokenDocument = newTokenDocumentOptional.orElseGet(() ->
            fcmTokenConverter.toDocument(new FcmTokenRequest(newToken, fcmTokenUpdateRequest.getDeviceType()))
        );

        // oldToken 의 토픽 및 상태값 복사
        newFcmTokenDocument.copyTopicsAndStatusFrom(oldFcmTokenDocument, fcmTokenUpdateRequest.getDeviceType());

        fcmTokenService.saveFcmToken(newFcmTokenDocument);
        fcmTokenService.deleteBy(oldToken);
        return true;
    }

    /**
     * 관리자가 사용자의 모든 토큰을 조회할 때 사용
     *
     * @return 사용자 토큰 목록
     */
    public List<FcmTokenInfo> getFcmTokenList() {
        List<FcmTokenDocument> fcmTokenDocumentList = fcmTokenService.getFcmTokenList();
        return fcmTokenConverter.toListResponse(fcmTokenDocumentList);
    }

}

