package com.school.school.service;

import com.school.school.entity.User;
import com.school.school.entity.StaffRole;
import com.school.school.pojo.AuthResponse;
import com.school.school.pojo.UserRequest;
import com.school.school.pojo.UserResponse;
import com.school.school.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;

    public UserService(UserRepository userRepository, AuthTokenService authTokenService) {
        this.userRepository = userRepository;
        this.authTokenService = authTokenService;
    }

    public AuthResponse registerUser(UserRequest request) {
        // 1. Check if the user already exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalStateException("Email already taken!");
        }

        // 2. Create a NEW user instance
        User user = new User();
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPassword(request.password());

        // 3. Save the new user to the database
        User savedUser = userRepository.save(user);
        return toAuthResponse(savedUser);
    }

    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!user.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return toAuthResponse(user);
    }

    public UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public List<UserResponse> getUsers(StaffRole role) {
        List<User> users = role == null
                ? userRepository.findAll()
                : userRepository.findByRoleOrderByFirstNameAscLastNameAsc(role);

        return users.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(authTokenService.createToken(user), "Bearer", mapToResponse(user));
    }
}
