package com.school.school.controller;

import com.school.school.pojo.ActivityRequest;
import com.school.school.pojo.ActivityResponse;
import com.school.school.service.ActivityService;
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
@RequestMapping("/api/v1/activities")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public List<ActivityResponse> getActivities(@RequestParam(required = false) UUID schoolId) {
        return activityService.getActivities(schoolId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityResponse createActivity(@RequestBody ActivityRequest request) {
        return activityService.createActivity(request);
    }

    @PutMapping("/{activityId}")
    public ActivityResponse updateActivity(@PathVariable UUID activityId, @RequestBody ActivityRequest request) {
        return activityService.updateActivity(activityId, request);
    }

    @DeleteMapping("/{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@PathVariable UUID activityId) {
        activityService.deleteActivity(activityId);
    }
}
