package com.school.school.controller;

import com.school.school.entity.User;
import com.school.school.pojo.SchoolRequest;
import com.school.school.pojo.SchoolResponse;
import com.school.school.pojo.UserRequest;
import com.school.school.repository.UserRepository;
import com.school.school.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> addSchool(@RequestBody UserRequest request) {
        User user = userService.registerUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
