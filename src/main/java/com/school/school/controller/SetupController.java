package com.school.school.controller;

import com.school.school.pojo.AuthResponse;
import com.school.school.pojo.SetupStatusResponse;
import com.school.school.pojo.UserRequest;
import com.school.school.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/setup")
public class SetupController {
    private final UserService userService;

    public SetupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/status")
    public SetupStatusResponse getStatus() {
        return new SetupStatusResponse(!userService.hasStaffAccounts());
    }

    @PostMapping("/admin")
    public ResponseEntity<AuthResponse> createInitialAdmin(@RequestBody UserRequest request) {
        return new ResponseEntity<>(userService.createInitialAdmin(request), HttpStatus.CREATED);
    }
}
