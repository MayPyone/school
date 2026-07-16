package com.school.school.pojo;

import com.school.school.entity.UserRole;

public record UserRequest(
        String firstName,
        String lastName,
        String email,
        UserRole role,
        String password

) {
}
