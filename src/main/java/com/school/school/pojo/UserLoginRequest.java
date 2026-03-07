package com.school.school.pojo;

public record UserLoginRequest(
        String email,
        String password
) {
}
