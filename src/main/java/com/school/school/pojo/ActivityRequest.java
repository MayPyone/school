package com.school.school.pojo;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ActivityRequest(
        UUID schoolId,
        String title,
        String description,
        LocalDate activityDate,
        String location,
        Integer maxParticipants,
        Integer registeredCount,
        List<String> images
) {
}
