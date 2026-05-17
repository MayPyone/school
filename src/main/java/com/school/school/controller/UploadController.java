package com.school.school.controller;

import com.school.school.pojo.ImageUploadPresignRequest;
import com.school.school.pojo.ImageUploadPresignResponse;
import com.school.school.service.SupabaseStorageService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/uploads")
public class UploadController {
    private final SupabaseStorageService supabaseStorageService;

    public UploadController(SupabaseStorageService supabaseStorageService) {
        this.supabaseStorageService = supabaseStorageService;
    }

    @PostMapping("/images/presign")
    public ImageUploadPresignResponse presignImageUpload(@RequestBody ImageUploadPresignRequest request) {
        return supabaseStorageService.presignImageUpload(request);
    }

    @PostMapping("/schools/{schoolId}/images/presign")
    public ImageUploadPresignResponse presignSchoolImageUpload(
            @PathVariable UUID schoolId,
            @RequestBody ImageUploadPresignRequest request
    ) {
        return supabaseStorageService.presignImageUpload(schoolId, request);
    }

    @DeleteMapping("/images")
    public void deleteImage(@RequestParam String publicUrl) {
        supabaseStorageService.deleteImage(publicUrl);
    }
}
