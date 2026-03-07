package com.school.school.pojo;

import java.util.UUID;

public record LessonRequest(
    UUID schoolId,
    String title,
    Byte levelId,
    String content,
    String category,
    UUID userId
) {
}
