package com.school.school.service;

import com.school.school.entity.User;
import com.school.school.pojo.UserRequest;
import com.school.school.pojo.UserResponse;
import com.school.school.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserRequest request) {
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
        user.setPassword(request.password()); // Note: You should encode this later!

        // 3. Save the new user to the database
        return userRepository.save(user);
    }
}
