package com.school.school.controller;

import com.school.school.config.AuthenticationInterceptor;
import com.school.school.entity.User;
import com.school.school.entity.UserRole;
import com.school.school.pojo.StaffRequest;
import com.school.school.pojo.StaffResponse;
import com.school.school.service.StaffService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/staff")
public class AdminStaffController {
    private final StaffService staffService;

    public AdminStaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StaffResponse createStaff(HttpServletRequest servletRequest, @RequestBody StaffRequest request) {
        return staffService.createStaff(actorRole(servletRequest), request);
    }

    @PutMapping("/{staffId}")
    public StaffResponse updateStaff(HttpServletRequest servletRequest, @PathVariable UUID staffId, @RequestBody StaffRequest request) {
        return staffService.updateStaff(actorRole(servletRequest), staffId, request);
    }

    @PutMapping("/{staffId}/revoke")
    public StaffResponse revokeStaff(HttpServletRequest servletRequest, @PathVariable UUID staffId) {
        return staffService.revokeStaff(actorRole(servletRequest), staffId);
    }

    @PutMapping("/{staffId}/restore")
    public StaffResponse restoreStaff(HttpServletRequest servletRequest, @PathVariable UUID staffId) {
        return staffService.restoreStaff(actorRole(servletRequest), staffId);
    }

    @DeleteMapping("/{staffId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStaff(HttpServletRequest servletRequest, @PathVariable UUID staffId) {
        staffService.deleteStaff(actorRole(servletRequest), staffId);
    }

    private UserRole actorRole(HttpServletRequest request) {
        User user = (User) request.getAttribute(AuthenticationInterceptor.AUTHENTICATED_USER_ATTRIBUTE);
        return user.getRole();
    }
}
