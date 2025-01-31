package api.domain.jwt.converter;

import api.common.exception.token.TokenException;
import api.domain.jwt.model.JwtTokenDto;
import api.domain.jwt.model.JwtTokenResponse;
import db.domain.token.jwt.JwtTokenDocument;
import global.annotation.Converter;
import global.errorcode.ErrorCode;
import java.util.Objects;

@Converter
public class JwtTokenConverter {

    public JwtTokenDocument toRefreshTokenEntity(String userId, String refreshToken) {
        return JwtTokenDocument.builder()
            .userId(userId)
            .refreshToken(refreshToken)
            .build();
    }

    public JwtTokenResponse toResponse(JwtTokenDto accessToken, JwtTokenDto refreshToken) {

        Objects.requireNonNull(accessToken, ()->{
            throw new TokenException(ErrorCode.NULL_POINT);
        });
        Objects.requireNonNull(refreshToken, ()->{
            throw new TokenException(ErrorCode.NULL_POINT);
        });

        return JwtTokenResponse.builder()
            .accessToken(accessToken.getToken())
            .accessTokenExpiredAt(accessToken.getExpiredAt())
            .refreshToken(refreshToken.getToken())
            .refreshTokenExpiredAt(refreshToken.getExpiredAt())
            .build();
    }
}
