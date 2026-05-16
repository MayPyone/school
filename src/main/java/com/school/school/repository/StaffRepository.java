package com.school.school.repository;

import com.school.school.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffRepository extends JpaRepository<Staff, UUID> {
    List<Staff> findAllByOrderByCreatedAtDesc();

    List<Staff> findBySchoolIdOrderByCreatedAtDesc(UUID schoolId);

    Optional<Staff> findByUserIdAndSchoolId(UUID userId, UUID schoolId);
}
