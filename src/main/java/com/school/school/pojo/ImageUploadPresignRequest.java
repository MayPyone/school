package com.school.school.pojo;

import java.util.UUID;

public record ImageUploadPresignRequest(
        String originalFilename,
        String contentType,
        String folder,
        UUID schoolId
) {
}
