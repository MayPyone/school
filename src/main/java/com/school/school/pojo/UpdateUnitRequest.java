package com.school.school.pojo;

import java.util.UUID;

public record UpdateUnitRequest(
        String title,
        String content,
        String videoUrl,
        UUID lessonId,
        UUID createdById,
        UUID id
) {
}
