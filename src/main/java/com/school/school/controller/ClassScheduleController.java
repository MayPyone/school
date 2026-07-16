package com.school.school.controller;

import com.school.school.pojo.ClassScheduleRequest;
import com.school.school.pojo.ClassScheduleResponse;
import com.school.school.service.ClassScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules")
public class ClassScheduleController {
    private final ClassScheduleService classScheduleService;

    public ClassScheduleController(ClassScheduleService classScheduleService) {
        this.classScheduleService = classScheduleService;
    }

    @GetMapping
    public List<ClassScheduleResponse> getSchedules(@RequestParam(required = false) String schoolId) {
        return classScheduleService.getSchedules(schoolId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClassScheduleResponse createSchedule(@RequestBody ClassScheduleRequest request) {
        return classScheduleService.createSchedule(request);
    }

    @PutMapping("/{scheduleId}")
    public ClassScheduleResponse updateSchedule(@PathVariable UUID scheduleId, @RequestBody ClassScheduleRequest request) {
        return classScheduleService.updateSchedule(scheduleId, request);
    }

    @DeleteMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(@PathVariable UUID scheduleId) {
        classScheduleService.deleteSchedule(scheduleId);
    }
}
