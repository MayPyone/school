package com.school.school.pojo;

import com.school.school.entity.StaffRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        StaffRole role
) {
}
