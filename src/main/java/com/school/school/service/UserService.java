package com.school.school.service;

import com.school.school.entity.User;
import com.school.school.entity.UserRole;
import com.school.school.pojo.AuthResponse;
import com.school.school.pojo.UserRequest;
import com.school.school.pojo.UserResponse;
import com.school.school.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;
import java.util.List;

@Service
public class UserService {
    private static final List<UserRole> STAFF_ACCOUNT_ROLES = List.copyOf(EnumSet.of(
            UserRole.SUPER_ADMIN,
            UserRole.ADMIN,
            UserRole.TEACHER,
            UserRole.ASSISTANT
    ));

    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AuthTokenService authTokenService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authTokenService = authTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse registerUser(UserRequest request) {
        if (request == null || isBlank(request.firstName()) || isBlank(request.lastName())
                || isBlank(request.email()) || isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "firstName, lastName, email, and password are required");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken!");
        }

        User user = new User();
        user.setEmail(request.email().trim());
        user.setRole(UserRole.END_USER);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);
        return toAuthResponse(savedUser);
    }

    public boolean hasStaffAccounts() {
        return userRepository.countByRoleIn(STAFF_ACCOUNT_ROLES) > 0;
    }

    public AuthResponse createInitialAdmin(UserRequest request) {
        if (hasStaffAccounts()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Initial super admin setup has already been completed");
        }

        if (isBlank(request.firstName()) || isBlank(request.lastName())
                || isBlank(request.email()) || isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "firstName, lastName, email, and password are required");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken!");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setRole(UserRole.SUPER_ADMIN);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPassword(passwordEncoder.encode(request.password()));

        return toAuthResponse(userRepository.save(user));
    }

    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordMatches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        if (!isEncodedPassword(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
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

    public List<UserResponse> getUsers(UserRole role) {
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

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (isBlank(storedPassword)) {
            return false;
        }

        if (isEncodedPassword(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }

        return storedPassword.equals(rawPassword);
    }

    private boolean isEncodedPassword(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}
