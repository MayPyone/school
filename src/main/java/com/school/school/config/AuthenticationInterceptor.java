package com.school.school.config;

import com.school.school.entity.User;
import com.school.school.service.AuthTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    public static final String AUTHENTICATED_USER_ATTRIBUTE = "authenticatedUser";
    public static final String AUTH_TOKEN_ATTRIBUTE = "authToken";

    private final AuthTokenService authTokenService;

    public AuthenticationInterceptor(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (isPublicRoute(request)) {
            return true;
        }

        Optional<String> token = authTokenService.extractBearerToken(request);
        Optional<User> user = token.flatMap(authTokenService::authenticate);
        if (user.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"message\":\"Missing or invalid bearer token\"}");
            return false;
        }

        request.setAttribute(AUTHENTICATED_USER_ATTRIBUTE, user.get());
        request.setAttribute(AUTH_TOKEN_ATTRIBUTE, token.get());
        return true;
    }

    private boolean isPublicRoute(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if (HttpMethod.OPTIONS.matches(method)) {
            return true;
        }

        if (HttpMethod.POST.matches(method) && "/api/v1/users".equals(path)) {
            return true;
        }

        if (HttpMethod.POST.matches(method) && "/api/v1/users/login".equals(path)) {
            return true;
        }

        if (HttpMethod.POST.matches(method) && "/api/v1/auth/login".equals(path)) {
            return true;
        }

        return path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
    }
}
