package api.domain.jwt.helper;

import api.common.error.JwtTokenErrorCode;
import api.common.exception.jwt.JwtTokenException;
import api.common.exception.jwt.JwtTokenExpiredException;
import api.common.exception.jwt.JwtTokenSignatureException;
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

    /**
     * 예외 발생을 Exception 최상위 예외를 가장 하위로 보내고, 구체적인 예외를 우선 잡는 방법으로 변경.<br>
     * instanceof 를 개별 catch 로 변경하여, catch 체이닝 방식을 통해 자동 분기 되도록 수정.<br>
     * Author:itstime0809
     */

    @Override
    public Map<String, Object> validationTokenWithThrow(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        JwtParser parser = Jwts.parser().verifyWith(key).build();

        try {
            Jws<Claims> result = parser.parseSignedClaims(token);
            return new HashMap<>(result.getPayload());
        } catch (SignatureException e) {
            // 토큰의 서명이 유효하지 않을 때
            throw new JwtTokenSignatureException(JwtTokenErrorCode.INVALID_TOKEN, e);
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었을 때
            throw new JwtTokenExpiredException(JwtTokenErrorCode.EXPIRED_TOKEN, e);
        } catch (Exception e) {
            // 그 외의 예외 처리
            throw new JwtTokenException(JwtTokenErrorCode.TOKEN_EXCEPTION, e);
        }
    }

    private JwtTokenDto getTokenDto(Map<String, Object> data, Long refreshTokenPlusHour) {
        LocalDateTime expiredLocalDateTime = LocalDateTime.now().plusHours(refreshTokenPlusHour);
        Date expiredAt = Date.from(expiredLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        String jwtToken = Jwts.builder()
            .signWith(key)
            .claims(data)
            .expiration(expiredAt)
            .compact();

        return JwtTokenDto.builder()
            .token(jwtToken)
            .expiredAt(expiredLocalDateTime)
            .build();
    }
}
