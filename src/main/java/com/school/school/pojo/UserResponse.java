package com.school.school.pojo;

public record UserResponse(
        String firstName,
        String lastName,
        String email,
        String id,
        String password,
        String role
) {
}
