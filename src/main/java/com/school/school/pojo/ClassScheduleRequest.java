package com.school.school.pojo;

import com.school.school.entity.DayOfWeek;
import com.school.school.entity.ScheduleMode;

import java.time.LocalTime;
import java.util.UUID;

public record ClassScheduleRequest(
        UUID schoolId,
        UUID teacherId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        ScheduleMode mode,
        String location,
        String meetingUrl
) {
}
