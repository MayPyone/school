package com.school.school.entity;

public enum UserRole {
    SUPER_ADMIN,
    ADMIN,
    TEACHER,
    ASSISTANT,
    END_USER;

    public static UserRole fromStaffRole(StaffRole role) {
        return UserRole.valueOf(role.name());
    }
}
