package com.school.school.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ActivityResponse(
        UUID id,
        UUID schoolId,
        String title,
        String description,
        LocalDate activityDate,
        String location,
        Integer maxParticipants,
        Integer registeredCount,
        List<String> images,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
