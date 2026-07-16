package com.school.school.controller;

import com.school.school.pojo.AuthResponse;
import com.school.school.pojo.UserLoginRequest;
import com.school.school.pojo.UserRequest;
import com.school.school.pojo.UserResponse;
import com.school.school.entity.UserRole;
import com.school.school.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(required = false) UserRole role) {
        return ResponseEntity.ok(userService.getUsers(role));
    }

    @PostMapping
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRequest request) {
        AuthResponse user = userService.registerUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping(path="/login")
    public ResponseEntity<AuthResponse> signInUser(@RequestBody UserLoginRequest request){
        AuthResponse user = userService.login(request.email(), request.password());
        return  new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }
}
