package com.school.school.pojo;

import java.util.UUID;

public record LessonUpdate(
        String title,
        Byte levelId,
        String content,
        String category
) {
}
