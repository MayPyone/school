package com.school.school.repository;

import com.school.school.entity.Lesson;
import com.school.school.entity.School;
import com.school.school.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findBySchoolId(UUID schoolId);
}