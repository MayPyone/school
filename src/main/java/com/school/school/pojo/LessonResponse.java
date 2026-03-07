package com.school.school.pojo;

import java.util.UUID;

public record LessonResponse(
        UUID lessonId,
        UUID schoolId,
        String title,
        String level,
        String content,
        String category,
        String createdBy
) {
}
