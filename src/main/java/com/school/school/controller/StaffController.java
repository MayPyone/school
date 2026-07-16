package com.school.school.controller;

import com.school.school.pojo.StaffRequest;
import com.school.school.pojo.StaffResponse;
import com.school.school.service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff")
public class StaffController {
    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public List<StaffResponse> getStaff(@RequestParam(required = false) String schoolId) {
        return staffService.getStaff(schoolId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StaffResponse createStaff(@RequestBody StaffRequest request) {
        return staffService.createStaff(request);
    }

    @PutMapping("/{staffId}")
    public StaffResponse updateStaff(@PathVariable UUID staffId, @RequestBody StaffRequest request) {
        return staffService.updateStaff(staffId, request);
    }

    @PutMapping("/{staffId}/revoke")
    public StaffResponse revokeStaff(@PathVariable UUID staffId) {
        return staffService.revokeStaff(staffId);
    }

    @DeleteMapping("/{staffId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStaff(@PathVariable UUID staffId) {
        staffService.deleteStaff(staffId);
    }
}
