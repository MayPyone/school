package com.school.school.service;

import com.school.school.config.SupabaseStorageProperties;
import com.school.school.pojo.ImageUploadPresignRequest;
import com.school.school.pojo.ImageUploadPresignResponse;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class SupabaseStorageService {
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final SupabaseStorageProperties properties;

    public SupabaseStorageService(SupabaseStorageProperties properties) {
        this.properties = properties;
    }

    public ImageUploadPresignResponse presignImageUpload(ImageUploadPresignRequest request) {
        validateConfiguration();
        String contentType = validateContentType(request.contentType());
        String key = buildObjectKey(request.folder(), request.originalFilename(), contentType);
        Duration signatureDuration = Duration.ofSeconds(properties.getUploadUrlDurationSeconds());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(signatureDuration)
                .putObjectRequest(putObjectRequest)
                .build();

        try (S3Presigner presigner = createPresigner()) {
            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return new ImageUploadPresignResponse(
                    key,
                    presignedRequest.url().toString(),
                    buildPublicUrl(key),
                    contentType,
                    Instant.now().plus(signatureDuration)
            );
        }
    }

    private S3Presigner createPresigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.getAccessKeyId(),
                properties.getSecretAccessKey()
        );

        return S3Presigner.builder()
                .region(Region.of(properties.getRegion()))
                .endpointOverride(URI.create(resolveEndpoint()))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .checksumValidationEnabled(false)
                        .build())
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    private String resolveEndpoint() {
        if (properties.getEndpoint() != null && !properties.getEndpoint().isBlank()) {
            return properties.getEndpoint();
        }

        return "https://" + properties.getProjectRef() + ".storage.supabase.co/storage/v1/s3";
    }

    private String validateContentType(String contentType) {
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalStateException("Only JPEG, PNG, WebP, and GIF images are supported");
        }
        return contentType.toLowerCase(Locale.ROOT);
    }

    private String buildObjectKey(String folder, String originalFilename, String contentType) {
        String safeFolder = sanitizeFolder(folder);
        LocalDate today = LocalDate.now();
        String extension = extensionFor(originalFilename, contentType);
        return safeFolder + "/" + today.getYear() + "/" + today.getMonthValue() + "/" + UUID.randomUUID() + extension;
    }

    private String sanitizeFolder(String folder) {
        if (folder == null || folder.isBlank()) {
            return "images";
        }

        String sanitized = folder.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9/_-]", "-")
                .replaceAll("/+", "/")
                .replaceAll("^/|/$", "");
        return sanitized.isBlank() ? "images" : sanitized;
    }

    private String extensionFor(String originalFilename, String contentType) {
        String fallback = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".img";
        };

        if (originalFilename == null) {
            return fallback;
        }

        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            return fallback;
        }

        String extension = originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);
        return extension.matches("\\.(jpg|jpeg|png|webp|gif)") ? extension : fallback;
    }

    private String buildPublicUrl(String key) {
        String baseUrl = properties.getPublicBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("Supabase public base URL is not configured");
        }
        return baseUrl.replaceAll("/+$", "") + "/" + key;
    }

    private void validateConfiguration() {
        if (isBlank(properties.getAccessKeyId())
                || isBlank(properties.getSecretAccessKey()) || isBlank(properties.getBucket())) {
            throw new IllegalStateException("Supabase storage configuration is incomplete");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
