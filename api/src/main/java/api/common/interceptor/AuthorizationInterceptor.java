package api.common.interceptor;

import api.common.error.UserErrorCode;
import api.common.exception.user.UserNotPermitted;
import api.domain.jwt.model.JwtTokenInfo;
import api.domain.jwt.service.JwtTokenService;
import db.domain.user.enums.UserRole;
import global.errorcode.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String X_USER_ID = "x-user-id";
    private static final String X_USER_ROLE = "x-user-role";


    private final JwtTokenService jwtTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // CORS preflight 요청의 경우, 인증 검증 없이 반환.
        if(HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        // 정적 리소스를 요청하는 경우, 아래 Handler 가 처리.
        // 정적 요청이 아닌 경우, 해당 분기문을 수행하지 않음.
        if(handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        // HTTP 헤더에서 Authorization 필드를 추출.
        String authorizationHeader = request.getHeader(AUTHORIZATION);


        // Header 에 AUTHORIZATION 헤더가 없거나 있지만 Bearer 시작하지 않는 경우 예외 발생
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("AuthorizationHeader is null or not contain bearer");
        }

        // Header 에 AUTHORIZATION 헤더가 있고, Bearer 시작한다면, 토큰 추출
        String accessToken = authorizationHeader.substring(7);


        // 토큰을 검증.
        JwtTokenInfo jwtTokenInfo = jwtTokenService.validationToken(accessToken);


        // 토큰 검증 결과가 null 이거나 ADMIN 권한이 아닌 경우 403 에러 응답
        if (jwtTokenInfo == null || !UserRole.ADMIN.equals(jwtTokenInfo.getRole())) {
            throw new UserNotPermitted(ErrorCode.BAD_REQUEST, "User not permitted");
        }

        // RequestContext 에 사용자 정보 저장
        RequestAttributes requestContext = RequestContextHolder.getRequestAttributes();
        if (requestContext != null) {
            requestContext.setAttribute(X_USER_ID, jwtTokenInfo.getUserId(), RequestAttributes.SCOPE_REQUEST);
            requestContext.setAttribute(X_USER_ROLE, jwtTokenInfo.getRole(), RequestAttributes.SCOPE_REQUEST);
        }

        return true;
    }
}
