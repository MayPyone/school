package com.school.school.controller;

import com.school.school.pojo.ImageUploadPresignRequest;
import com.school.school.pojo.ImageUploadPresignResponse;
import com.school.school.service.SupabaseStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
