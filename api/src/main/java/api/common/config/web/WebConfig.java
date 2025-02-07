package api.common.config.web;

import api.common.interceptor.AuthorizationInterceptor;
import api.common.resolver.AuthenticatedUserResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthorizationInterceptor authorizationInterceptor;

    private final List<String> WHITE_LIST = List.of("/open-api/**");

    private final List<String> URL = List.of(
        "http://localhost:8001",
        "http://127.0.0.1:5500",
        "https://www.knutice.site"
    );

    private final List<String> METHODS = List.of("GET", "POST", "OPTIONS");

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 적용
            .allowedOrigins(URL.toArray(new String[0])) // 허용할 도메인
            .allowedMethods(METHODS.toArray(new String[0])) // 허용할 HTTP 메서드
            .allowedHeaders("*") // 모든 헤더 허용
            .allowCredentials(true); // 인증 정보 포함 허용
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthenticatedUserResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(WHITE_LIST)
        ;
    }

}
