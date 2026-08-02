package com.school.school.pojo;

import com.school.school.entity.DayOfWeek;
import com.school.school.entity.ScheduleMode;
import com.school.school.entity.StaffRole;
import com.school.school.entity.StaffStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record SchoolDataExport(
        int version,
        Instant exportedAt,
        SchoolResponse school,
        List<OpeningHourArchive> openingHours,
        List<LessonArchive> lessons,
        List<ClassScheduleArchive> schedules,
        List<StaffArchive> staff,
        List<ActivityArchive> activities
) {
    public record OpeningHourArchive(
            DayOfWeek dayOfWeek,
            LocalTime openTime,
            LocalTime closeTime
    ) {
    }

    public record LessonArchive(
            UUID sourceId,
            String title,
            String category,
            String content,
            String levelName,
            String createdByEmail,
            List<UnitArchive> units
    ) {
    }

    public record UnitArchive(
            UUID sourceId,
            String title,
            String content,
            String videoUrl,
            String createdByEmail
    ) {
    }

    public record ClassScheduleArchive(
            UUID sourceId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            ScheduleMode mode,
            String teacherEmail,
            String location,
            String meetingUrl
    ) {
    }

    public record StaffArchive(
            UUID sourceId,
            UUID sourceUserId,
            String firstName,
            String lastName,
            String email,
            String phone,
            StaffRole role,
            LocalDate hireDate,
            StaffStatus status
    ) {
    }

    public record ActivityArchive(
            UUID sourceId,
            String title,
            String description,
            List<String> images,
            LocalDate activityDate,
            String location,
            Integer maxParticipants,
            Integer registeredCount
    ) {
    }
}
