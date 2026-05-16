package com.school.school.repository;

import com.school.school.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, UUID> {
    List<ClassSchedule> findBySchoolIdOrderByDayOfWeekAscStartTimeAsc(UUID schoolId);

    List<ClassSchedule> findAllByOrderByDayOfWeekAscStartTimeAsc();
}
