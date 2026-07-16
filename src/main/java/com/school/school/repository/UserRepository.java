package com.school.school.repository;

import com.school.school.entity.User;
import com.school.school.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    List<User> findByRoleOrderByFirstNameAscLastNameAsc(UserRole role);

    long countByRoleIn(List<UserRole> roles);
}
