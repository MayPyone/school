package com.school.school.controller;

import com.school.school.pojo.SchoolResponse;
import com.school.school.service.SchoolDataArchiveService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/schools")
public class AdminSchoolController {
    private final SchoolDataArchiveService schoolDataArchiveService;

    public AdminSchoolController(SchoolDataArchiveService schoolDataArchiveService) {
        this.schoolDataArchiveService = schoolDataArchiveService;
    }

    @GetMapping("/{schoolId}/export")
    public ResponseEntity<byte[]> exportSchool(@PathVariable UUID schoolId) throws IOException {
        byte[] archive = schoolDataArchiveService.exportSchool(schoolId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("school-data.zip")
                        .build()
                        .toString())
                .body(archive);
    }

    @PostMapping(path = "/{schoolId}/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SchoolResponse importSchool(@PathVariable UUID schoolId, @RequestPart("file") MultipartFile file) throws IOException {
        return schoolDataArchiveService.importSchool(schoolId, file);
    }
}
