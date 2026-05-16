package com.school.school.pojo;

public record ImageUploadPresignRequest(
        String originalFilename,
        String contentType,
        String folder
) {
}
