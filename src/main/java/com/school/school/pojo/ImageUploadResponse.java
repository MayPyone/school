package com.school.school.pojo;

public record ImageUploadResponse(
        String key,
        String publicUrl,
        String contentType
) {
}
