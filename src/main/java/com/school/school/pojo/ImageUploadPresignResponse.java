package com.school.school.pojo;

import java.time.Instant;

public record ImageUploadPresignResponse(
        String key,
        String uploadUrl,
        String publicUrl,
        String contentType,
        Instant expiresAt
) {
}
