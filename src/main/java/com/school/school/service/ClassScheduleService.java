package com.school.school.service;

import com.school.school.entity.ClassSchedule;
import com.school.school.entity.School;
import com.school.school.entity.StaffRole;
import com.school.school.entity.User;
import com.school.school.pojo.ClassScheduleRequest;
import com.school.school.pojo.ClassScheduleResponse;
import com.school.school.repository.ClassScheduleRepository;
import com.school.school.repository.SchoolRepository;
import com.school.school.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClassScheduleService {
    private final ClassScheduleRepository classScheduleRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;

    public ClassScheduleService(ClassScheduleRepository classScheduleRepository, SchoolRepository schoolRepository, UserRepository userRepository) {
        this.classScheduleRepository = classScheduleRepository;
        this.schoolRepository = schoolRepository;
        this.userRepository = userRepository;
    }

    public List<ClassScheduleResponse> getSchedules(UUID schoolId) {
        List<ClassSchedule> schedules = schoolId == null
                ? classScheduleRepository.findAllByOrderByDayOfWeekAscStartTimeAsc()
                : classScheduleRepository.findBySchoolIdOrderByDayOfWeekAscStartTimeAsc(schoolId);

        return schedules.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ClassScheduleResponse createSchedule(ClassScheduleRequest request) {
        validateRequiredFields(request);

        School school = schoolRepository.findById(request.schoolId())
                .orElseThrow(() -> new IllegalStateException("School not found"));

        ClassSchedule schedule = new ClassSchedule();
        schedule.setSchool(school);
        applyRequest(schedule, request);

        return mapToResponse(classScheduleRepository.save(schedule));
    }

    @Transactional
    public ClassScheduleResponse updateSchedule(UUID scheduleId, ClassScheduleRequest request) {
        ClassSchedule schedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalStateException("Class schedule not found"));

        if (request.schoolId() != null) {
            School school = schoolRepository.findById(request.schoolId())
                    .orElseThrow(() -> new IllegalStateException("School not found"));
            schedule.setSchool(school);
        }

        applyRequest(schedule, request);
        validateTimeRange(schedule);

        return mapToResponse(schedule);
    }

    public void deleteSchedule(UUID scheduleId) {
        ClassSchedule schedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalStateException("Class schedule not found"));

        classScheduleRepository.deleteById(schedule.getId());
    }

    private void applyRequest(ClassSchedule schedule, ClassScheduleRequest request) {
        if (request.dayOfWeek() != null) {
            schedule.setDayOfWeek(request.dayOfWeek());
        }
        if (request.startTime() != null) {
            schedule.setStartTime(request.startTime());
        }
        if (request.endTime() != null) {
            schedule.setEndTime(request.endTime());
        }
        if (request.mode() != null) {
            schedule.setMode(request.mode());
        }
        if (request.location() != null) {
            schedule.setLocation(request.location());
        }
        if (request.meetingUrl() != null) {
            schedule.setMeetingUrl(request.meetingUrl());
        }
        applyTeacher(schedule, request.teacherId());
    }

    private void applyTeacher(ClassSchedule schedule, UUID teacherId) {
        if (teacherId == null) {
            return;
        }

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalStateException("Teacher not found"));

        if (teacher.getRole() != StaffRole.TEACHER) {
            throw new IllegalStateException("Class schedule teacher must have TEACHER role");
        }

        schedule.setTeacher(teacher);
    }

    private void validateRequiredFields(ClassScheduleRequest request) {
        if (request.schoolId() == null || request.dayOfWeek() == null || request.startTime() == null
                || request.endTime() == null || request.mode() == null) {
            throw new IllegalStateException("schoolId, dayOfWeek, startTime, endTime, and mode are required");
        }

        if (!request.startTime().isBefore(request.endTime())) {
            throw new IllegalStateException("startTime must be before endTime");
        }
    }

    private void validateTimeRange(ClassSchedule schedule) {
        if (schedule.getStartTime() != null && schedule.getEndTime() != null
                && !schedule.getStartTime().isBefore(schedule.getEndTime())) {
            throw new IllegalStateException("startTime must be before endTime");
        }
    }

    private ClassScheduleResponse mapToResponse(ClassSchedule schedule) {
        return new ClassScheduleResponse(
                schedule.getId(),
                schedule.getSchool().getId(),
                schedule.getTeacher() != null ? schedule.getTeacher().getId() : null,
                schedule.getTeacher() != null ? schedule.getTeacher().getFirstName() + " " + schedule.getTeacher().getLastName() : null,
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getMode(),
                schedule.getLocation(),
                schedule.getMeetingUrl(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }
}
