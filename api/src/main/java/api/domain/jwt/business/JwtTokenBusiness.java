package api.domain.jwt.business;

import api.common.error.JwtTokenErrorCode;
import api.common.exception.token.TokenException;
import api.common.exception.token.TokenSignatureException;
import api.domain.jwt.converter.JwtTokenConverter;
import api.domain.jwt.model.JwtTokenDto;
import api.domain.jwt.model.JwtTokenResponse;
import api.domain.jwt.model.JwtTokenValidationRequest;
import api.domain.jwt.model.JwtTokenValidationResponse;
import api.domain.jwt.service.JwtTokenService;
import db.domain.token.jwt.JwtTokenDocument;
import db.domain.user.UserDocument;
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
            throw new TokenException(ErrorCode.NULL_POINT);
        }

        String userId = userDocument.getId();

        JwtTokenDto accessToken = jwtTokenService.issueAccessToken(userId);

        JwtTokenDto refreshToken = jwtTokenService.issueRefreshToken(userId);

        JwtTokenDocument tokenEntity = jwtTokenConverter.toRefreshTokenEntity(
            userDocument.getId(), refreshToken.getToken());

        jwtTokenService.deleteRefreshToken(userId);

        jwtTokenService.saveRefreshToken(tokenEntity);

        return jwtTokenConverter.toResponse(accessToken, refreshToken);
    }

    public JwtTokenDto reIssueAccessToken(String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) { // 헤더 검증
            String token = refreshToken.substring(7);
            return jwtTokenService.reIssueAccessToken(token);
        }
        throw new TokenSignatureException(JwtTokenErrorCode.INVALID_TOKEN);
    }

    public JwtTokenValidationResponse tokenValidation(JwtTokenValidationRequest token) {
        String userId = jwtTokenService.validationToken(
            token.getJwtTokenDto().getToken().substring(7));
        return JwtTokenValidationResponse.builder().userId(userId).build();
    }
}
