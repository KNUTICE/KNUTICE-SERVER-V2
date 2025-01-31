package api.domain.jwt.service;

import api.common.error.JwtTokenErrorCode;
import api.common.exception.token.TokenException;
import api.domain.jwt.ifs.JwtTokenHelperIfs;
import api.domain.jwt.model.JwtTokenDto;
import db.domain.token.jwt.JwtTokenDocument;
import db.domain.token.jwt.JwtTokenMongoRepository;
import global.errorcode.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtTokenHelperIfs jwtTokenHelperIfs;
    private final JwtTokenMongoRepository jwtTokenMongoRepository;

    public JwtTokenDto issueAccessToken(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        return jwtTokenHelperIfs.issueAccessToken(data);
    }

    public JwtTokenDto issueRefreshToken(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        return jwtTokenHelperIfs.issueRefresh(data);
    }

    public JwtTokenDto reIssueAccessToken(String refreshToken) {
        String userId = validationToken(refreshToken);

        JwtTokenDocument jwtTokenDocument = jwtTokenMongoRepository.findById(userId)
            .orElseThrow(() -> new TokenException(JwtTokenErrorCode.INVALID_TOKEN));

        String storedRefreshToken = jwtTokenDocument.getRefreshToken();

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new TokenException(JwtTokenErrorCode.INVALID_TOKEN);
        }
        return issueRefreshToken(userId);
    }

    public String validationToken(String token) {
        Map<String, Object> userData = jwtTokenHelperIfs.validationTokenWithThrow(token);

        Object userId = userData.get("userId");
        Objects.requireNonNull(userId, () -> {
            throw new TokenException(ErrorCode.NULL_POINT);
        });
        return userId.toString();
    }

    public void saveRefreshToken(JwtTokenDocument jwtTokenDocument) {
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("userId", jwtTokenDocument.getUserId());
        tokenData.put("refreshToken", jwtTokenDocument.getRefreshToken());
        jwtTokenMongoRepository.save(jwtTokenDocument);
    }

    public void deleteRefreshToken(String userId) {
        jwtTokenMongoRepository.deleteById(userId);
    }

}
