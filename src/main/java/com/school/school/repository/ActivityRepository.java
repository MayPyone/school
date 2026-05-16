package com.school.school.repository;

import com.school.school.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findAllByOrderByActivityDateDescCreatedAtDesc();

    List<Activity> findBySchoolIdOrderByActivityDateDescCreatedAtDesc(UUID schoolId);
}
