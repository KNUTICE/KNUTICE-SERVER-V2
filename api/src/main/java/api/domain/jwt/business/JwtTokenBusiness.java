package api.domain.jwt.business;

import api.common.error.JwtTokenErrorCode;
import api.common.exception.jwt.JwtTokenException;
import api.common.exception.jwt.JwtTokenSignatureException;
import api.domain.jwt.converter.JwtTokenConverter;
import api.domain.jwt.model.JwtTokenDto;
import api.domain.jwt.model.JwtTokenInfo;
import api.domain.jwt.model.JwtTokenResponse;
import api.domain.jwt.model.JwtTokenValidationRequest;
import api.domain.jwt.model.JwtTokenValidationResponse;
import api.domain.jwt.service.JwtTokenService;
import db.domain.token.jwt.JwtTokenDocument;
import db.domain.user.UserDocument;
import db.domain.user.enums.UserRole;
import global.annotation.Business;
import global.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Business
@RequiredArgsConstructor
public class JwtTokenBusiness {

    private final JwtTokenService jwtTokenService;
    private final JwtTokenConverter jwtTokenConverter;

    @Transactional
    public JwtTokenResponse issueToken(UserDocument userDocument) {

        if (userDocument == null) {
            throw new JwtTokenException(ErrorCode.NULL_POINT);
        }

        String userId = userDocument.getId();

        JwtTokenInfo jwtTokenInfo = JwtTokenInfo.builder()
            .userId(userId)
            .role(userDocument.getRole())
            .build();

        JwtTokenDto accessToken = jwtTokenService.issueAccessToken(jwtTokenInfo);
        JwtTokenDto refreshToken = jwtTokenService.issueRefreshToken(jwtTokenInfo);

        JwtTokenDocument jwtTokenDocument = jwtTokenConverter.toRefreshTokenDocument(
            userDocument.getId(), refreshToken.getToken());

        jwtTokenService.deleteRefreshToken(userId);

        jwtTokenService.saveRefreshToken(jwtTokenDocument);

        return jwtTokenConverter.toResponse(accessToken, refreshToken);
    }

    public JwtTokenDto reIssueAccessToken(String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) { // 헤더 검증
            String token = refreshToken.substring(7);
            return jwtTokenService.reIssueAccessToken(token);
        }
        throw new JwtTokenSignatureException(JwtTokenErrorCode.INVALID_TOKEN);
    }

    public JwtTokenValidationResponse tokenValidation(JwtTokenValidationRequest token) {
        JwtTokenInfo jwtTokenInfo = jwtTokenService.validationToken(token.getToken().substring(7));
        return JwtTokenValidationResponse.builder().userId(jwtTokenInfo.getUserId()).build();
    }

}
