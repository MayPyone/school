package com.school.school.pojo;

import com.school.school.entity.StaffRole;

public record UserRequest(
        String firstName,
        String lastName,
        String email,
        StaffRole role,
        String password

) {
}
