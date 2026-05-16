package com.school.school.pojo;

import com.school.school.entity.DayOfWeek;
import com.school.school.entity.ScheduleMode;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ClassScheduleResponse(
        UUID id,
        UUID schoolId,
        UUID teacherId,
        String teacherName,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        ScheduleMode mode,
        String location,
        String meetingUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
