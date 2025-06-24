package api.common.interceptor;

import api.common.error.UserErrorCode;
import api.common.exception.user.UserNotPermitted;
import api.domain.jwt.model.JwtTokenInfo;
import api.domain.jwt.service.JwtTokenService;
import db.domain.user.enums.UserRole;
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

    public static final String AUTHORIZATION = "Authorization";
    public static final String X_USER_ID = "x-user-id";
    public static final String X_USER_ROLE = "x-user-role";


    private final JwtTokenService jwtTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        //Authorization Header 추출
        String accessToken = request.getHeader(AUTHORIZATION);

        JwtTokenInfo jwtTokenInfo = jwtTokenService.validationToken(accessToken.substring(7));

        if (!jwtTokenInfo.getRole().equals(UserRole.ADMIN)) {
            throw new UserNotPermitted(UserErrorCode.USER_NOT_PERMITTED);
        }

        RequestAttributes requestContext = Objects.requireNonNull(
            RequestContextHolder.getRequestAttributes());
        requestContext.setAttribute(X_USER_ID, jwtTokenInfo.getUserId(),
            RequestAttributes.SCOPE_REQUEST);
        requestContext.setAttribute(X_USER_ROLE, jwtTokenInfo.getRole(),
            RequestAttributes.SCOPE_REQUEST);

        return true;
    }
}
