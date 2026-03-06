package com.school.school.controller;
import com.school.school.entity.School;
import com.school.school.pojo.SchoolRequest;
import com.school.school.pojo.SchoolResponse;
import com.school.school.pojo.SchoolUpdate;
import com.school.school.service.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schools")
public class SchoolController {
    private final SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @PostMapping
    public ResponseEntity<SchoolResponse> addSchool(@RequestBody SchoolRequest request) {
        return new ResponseEntity<>(schoolService.createSchool(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<School>> listSchools() {
        return ResponseEntity.ok(schoolService.getAllSchools());
    }

    @PutMapping(path= "{id}")
    public ResponseEntity<SchoolResponse> updateSchool (@PathVariable("id") UUID id,@RequestBody SchoolUpdate request) {
        return ResponseEntity.ok(schoolService.updateSchool(request, id));
    }

    @DeleteMapping(path = "{id}")
    public  void deleteSchool (@PathVariable ("id") UUID id) {
          schoolService.deleteSchool(id);
    }
}