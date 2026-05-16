package com.school.school.pojo;

import com.school.school.entity.StaffRole;
import com.school.school.entity.StaffStatus;

import java.time.LocalDate;
import java.util.UUID;

public record StaffRequest(
        UUID schoolId,
        String firstName,
        String lastName,
        String email,
        String phone,
        StaffRole role,
        LocalDate hireDate,
        StaffStatus status
) {
}
