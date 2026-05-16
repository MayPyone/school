package com.school.school.pojo;

import com.school.school.entity.StaffRole;
import com.school.school.entity.StaffStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record StaffResponse(
        UUID id,
        UUID schoolId,
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phone,
        StaffRole role,
        LocalDate hireDate,
        StaffStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
