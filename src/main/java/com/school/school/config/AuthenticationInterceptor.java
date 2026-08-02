package com.school.school.config;

import com.school.school.entity.User;
import com.school.school.entity.UserRole;
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

        User authenticatedUser = user.get();
        if (!isAllowedForRole(request, authenticatedUser.getRole())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"message\":\"This account does not have access to that resource\"}");
            return false;
        }

        request.setAttribute(AUTHENTICATED_USER_ATTRIBUTE, authenticatedUser);
        request.setAttribute(AUTH_TOKEN_ATTRIBUTE, token.get());
        return true;
    }

    private boolean isAllowedForRole(HttpServletRequest request, UserRole role) {
        if (isSuperAdminOnlyRoute(request)) {
            return role == UserRole.SUPER_ADMIN;
        }

        if (isAdminOnlyRoute(request)) {
            return role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN;
        }

        if (role != UserRole.END_USER) {
            return true;
        }

        String method = request.getMethod();
        String path = request.getRequestURI();

        if (HttpMethod.POST.matches(method) && "/api/v1/auth/logout".equals(path)) {
            return true;
        }

        if (!HttpMethod.GET.matches(method)) {
            return false;
        }

        return path.matches("^/api/v1/schools/?$")
                || path.matches("^/api/v1/schools/[^/]+/?$")
                || path.matches("^/api/v1/schools/[^/]+/lessons(/[^/]+)?/?$")
                || path.matches("^/api/v1/schools/[^/]+/schedules/?$")
                || path.matches("^/api/v1/schools/[^/]+/staff/?$")
                || path.matches("^/api/v1/schools/[^/]+/activities/?$");
    }

    private boolean isAdminOnlyRoute(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        return (HttpMethod.POST.matches(method) && path.matches("^/api/v1/admin/staff/?$"))
                || (HttpMethod.PUT.matches(method) && path.matches("^/api/v1/admin/staff/[^/]+(/(revoke|restore))?/?$"))
                || (HttpMethod.DELETE.matches(method) && path.matches("^/api/v1/admin/staff/[^/]+/?$"));
    }

    private boolean isSuperAdminOnlyRoute(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        return (HttpMethod.GET.matches(method) && path.matches("^/api/v1/admin/schools/[^/]+/export/?$"))
                || (HttpMethod.POST.matches(method) && path.matches("^/api/v1/admin/schools/[^/]+/import/?$"));
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

        if (HttpMethod.GET.matches(method) && "/api/v1/setup/status".equals(path)) {
            return true;
        }

        if (HttpMethod.POST.matches(method) && "/api/v1/setup/admin".equals(path)) {
            return true;
        }

        if (HttpMethod.GET.matches(method) && isPublicReadRoute(path)) {
            return true;
        }

        return path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
    }

    private boolean isPublicReadRoute(String path) {
        return path.matches("^/api/v1/schools/?$")
                || path.matches("^/api/v1/schools/[^/]+/?$")
                || path.matches("^/api/v1/schools/[^/]+/lessons(/[^/]+)?/?$")
                || path.matches("^/api/v1/schools/[^/]+/schedules/?$")
                || path.matches("^/api/v1/schools/[^/]+/staff/?$")
                || path.matches("^/api/v1/schools/[^/]+/activities/?$")
                || path.matches("^/api/v1/lessons/[^/]+/units(/[^/]+)?/?$")
                || path.matches("^/api/v1/activities/?$")
                || path.matches("^/api/v1/schedules/?$");
    }
}
