package com.school.school.pojo;

import org.antlr.v4.runtime.misc.NotNull;

public record CreateUnitRequest(
        String title,
        String content,
        String videoUrl,
        String lessonId,
        String createdById
) {}