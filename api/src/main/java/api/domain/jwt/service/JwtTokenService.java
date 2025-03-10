package api.domain.jwt.service;

import api.common.error.JwtTokenErrorCode;
import api.common.exception.jwt.JwtTokenException;
import api.domain.jwt.ifs.JwtTokenHelperIfs;
import api.domain.jwt.model.JwtTokenDto;
import api.domain.jwt.model.JwtTokenInfo;
import db.domain.token.jwt.JwtTokenDocument;
import db.domain.token.jwt.JwtTokenMongoRepository;
import db.domain.user.enums.UserRole;
import global.errorcode.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtTokenHelperIfs jwtTokenHelperIfs;
    private final JwtTokenMongoRepository jwtTokenMongoRepository;

    public JwtTokenDto issueAccessToken(JwtTokenInfo jwtTokenInfo) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", jwtTokenInfo.getUserId());
        data.put("role", jwtTokenInfo.getRole());
        return jwtTokenHelperIfs.issueAccessToken(data);
    }

    public JwtTokenDto issueRefreshToken(JwtTokenInfo jwtTokenInfo) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", jwtTokenInfo.getUserId());
        data.put("role", jwtTokenInfo.getRole());
        return jwtTokenHelperIfs.issueRefresh(data);
    }

    public JwtTokenDto reIssueAccessToken(String refreshToken) {
        JwtTokenInfo jwtTokenInfo = validationToken(refreshToken);

        JwtTokenDocument jwtTokenDocument = jwtTokenMongoRepository.findById(jwtTokenInfo.getUserId())
            .orElseThrow(() -> new JwtTokenException.JwtTokenDocumentException(JwtTokenErrorCode.INVALID_TOKEN));

        String storedRefreshToken = jwtTokenDocument.getRefreshToken();

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new JwtTokenException.JwtTokenInvalidException(JwtTokenErrorCode.INVALID_TOKEN);
        }
        return issueRefreshToken(JwtTokenInfo.builder()
            .userId(jwtTokenDocument.getUserId())
            .role(jwtTokenDocument.getRole())
            .build());
    }

    public JwtTokenInfo validationToken(String token) {
        Map<String, Object> userData = jwtTokenHelperIfs.validationTokenWithThrow(token);

        Object userId = userData.get("userId");
        Object userRole = userData.get("role");

        /**
         * userId, userRole 모두 토큰 생성시에 클레임에 포함시키는 값이므로
         * 토큰 생성시에 본 키 값이 변경된다면, get()이 null 을 발생시킬 수 있음.
         * 따라서, 그에 대한 방어 코드.
         */
        if (userId == null || userRole == null) {
            throw new JwtTokenException.JwtTokenMissingClaimException(ErrorCode.NULL_POINT);
        }

        return JwtTokenInfo.builder()
            .userId(userId.toString())
            .role(UserRole.valueOf(userRole.toString()))
            .build();
    }

    public void saveRefreshToken(JwtTokenDocument jwtTokenDocument) {
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("userId", jwtTokenDocument.getUserId());
        tokenData.put("role", jwtTokenDocument.getRole());
        tokenData.put("refreshToken", jwtTokenDocument.getRefreshToken());
        jwtTokenMongoRepository.save(jwtTokenDocument);
    }

    public void deleteRefreshToken(String userId) {
        jwtTokenMongoRepository.deleteById(userId);
    }

}
