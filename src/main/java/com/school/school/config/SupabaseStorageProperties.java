package com.school.school.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.supabase.storage")
public class SupabaseStorageProperties {
    private String projectRef;
    private String endpoint;
    private String accessKeyId;
    private String secretAccessKey;
    private String bucket;
    private String publicBaseUrl;
    private String region = "us-east-1";
    private long uploadUrlDurationSeconds = 300;

    public String getProjectRef() {
        return projectRef;
    }

    public void setProjectRef(String projectRef) {
        this.projectRef = projectRef;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getUploadUrlDurationSeconds() {
        return uploadUrlDurationSeconds;
    }

    public void setUploadUrlDurationSeconds(long uploadUrlDurationSeconds) {
        this.uploadUrlDurationSeconds = uploadUrlDurationSeconds;
    }
}
