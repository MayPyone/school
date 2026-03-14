package com.school.school.pojo;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.UUID;

public record CreateUnitRequest(
        String title,
        String content,
        String videoUrl,
        UUID lessonId,
        UUID createdById
) {}