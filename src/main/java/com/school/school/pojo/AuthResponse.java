package com.school.school.pojo;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {
}
