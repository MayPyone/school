package com.school.school.pojo;

import java.util.UUID;

public record UnitResponse(
        UUID id,
        String title,
        String content,
        String videoUrl,
        String lessonId,
        String createdById
) {

}
