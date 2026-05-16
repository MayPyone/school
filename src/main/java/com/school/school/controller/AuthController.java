package com.school.school.controller;

import com.school.school.config.AuthenticationInterceptor;
import com.school.school.entity.User;
import com.school.school.pojo.AuthResponse;
import com.school.school.pojo.UserLoginRequest;
import com.school.school.pojo.UserResponse;
import com.school.school.service.AuthTokenService;
import com.school.school.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final AuthTokenService authTokenService;

    public AuthController(UserService userService, AuthTokenService authTokenService) {
        this.userService = userService;
        this.authTokenService = authTokenService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody UserLoginRequest request) {
        return userService.login(request.email(), request.password());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        Object token = request.getAttribute(AuthenticationInterceptor.AUTH_TOKEN_ATTRIBUTE);
        if (token instanceof String authToken) {
            authTokenService.revoke(authToken);
        }
    }

    @GetMapping("/me")
    public UserResponse me(HttpServletRequest request) {
        User user = (User) request.getAttribute(AuthenticationInterceptor.AUTHENTICATED_USER_ATTRIBUTE);
        return userService.mapToResponse(user);
    }
}
