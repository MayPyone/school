package com.school.school.pojo;

public record UpdateUnitRequest(
        String title,
        String content,
        String videoUrl,
        String lessonId,
        String createdById
) {
}
