package api.domain.jwt.helper;

import api.common.error.JwtTokenErrorCode;
import api.common.exception.token.TokenException;
import api.common.exception.token.TokenExpiredException;
import api.common.exception.token.TokenSignatureException;
import api.domain.jwt.ifs.JwtTokenHelperIfs;
import api.domain.jwt.model.JwtTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenHelper implements JwtTokenHelperIfs {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access-token.plus-hour}")
    private Long accessTokenPlusHour;

    @Value("${jwt.refresh-token.plus-hour}")
    private Long refreshTokenPlusHour;

    @Override
    public JwtTokenDto issueAccessToken(Map<String, Object> data) {
        return getTokenDto(data, accessTokenPlusHour);
    }

    @Override
    public JwtTokenDto issueRefresh(Map<String, Object> data) {
        return getTokenDto(data, refreshTokenPlusHour);
    }

    @Override
    public Map<String, Object> validationTokenWithThrow(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        JwtParser parser = Jwts.parser().verifyWith(key).build();

        try {
            Jws<Claims> result = parser.parseClaimsJws(token);
            return new HashMap<>(result.getBody());
        } catch (Exception e) {
            if (e instanceof SignatureException) { // 토큰이 유효하지 않을 때
                throw new TokenSignatureException(JwtTokenErrorCode.INVALID_TOKEN);
            } else if (e instanceof ExpiredJwtException) { // 토큰 만료
                throw new TokenExpiredException(JwtTokenErrorCode.EXPIRED_TOKEN);
            } else { // 그 외
                throw new TokenException(JwtTokenErrorCode.TOKEN_EXCEPTION);
            }
        }
    }

    private JwtTokenDto getTokenDto(Map<String, Object> data, Long refreshTokenPlusHour) {
        LocalDateTime expiredLocalDateTime = LocalDateTime.now().plusHours(refreshTokenPlusHour);
        Date expiredAt = Date.from(expiredLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        String jwtToken = Jwts.builder()
            .signWith(key, SignatureAlgorithm.HS256)
            .setClaims(data)
            .setExpiration(expiredAt)
            .compact();

        return JwtTokenDto.builder()
            .token(jwtToken)
            .expiredAt(expiredLocalDateTime)
            .build();
    }
}
