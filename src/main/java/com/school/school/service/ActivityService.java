package com.school.school.service;

import com.school.school.entity.Activity;
import com.school.school.entity.School;
import com.school.school.pojo.ActivityRequest;
import com.school.school.pojo.ActivityResponse;
import com.school.school.repository.ActivityRepository;
import com.school.school.repository.SchoolRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final SchoolRepository schoolRepository;

    public ActivityService(ActivityRepository activityRepository, SchoolRepository schoolRepository) {
        this.activityRepository = activityRepository;
        this.schoolRepository = schoolRepository;
    }

    public List<ActivityResponse> getActivities(UUID schoolId) {
        List<Activity> activities = schoolId == null
                ? activityRepository.findAllByOrderByActivityDateDescCreatedAtDesc()
                : activityRepository.findBySchoolIdOrderByActivityDateDescCreatedAtDesc(schoolId);

        return activities.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ActivityResponse createActivity(ActivityRequest request) {
        if (request.schoolId() == null || request.title() == null || request.title().isBlank()) {
            throw new IllegalStateException("schoolId and title are required");
        }

        School school = schoolRepository.findById(request.schoolId())
                .orElseThrow(() -> new IllegalStateException("School not found"));

        Activity activity = new Activity();
        activity.setSchool(school);
        applyRequest(activity, request);

        return mapToResponse(activityRepository.save(activity));
    }

    @Transactional
    public ActivityResponse updateActivity(UUID activityId, ActivityRequest request) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalStateException("Activity not found"));

        if (request.schoolId() != null) {
            School school = schoolRepository.findById(request.schoolId())
                    .orElseThrow(() -> new IllegalStateException("School not found"));
            activity.setSchool(school);
        }

        applyRequest(activity, request);
        return mapToResponse(activity);
    }

    public void deleteActivity(UUID activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalStateException("Activity not found"));
        activityRepository.deleteById(activity.getId());
    }

    private void applyRequest(Activity activity, ActivityRequest request) {
        if (request.title() != null) {
            activity.setTitle(request.title());
        }
        if (request.description() != null) {
            activity.setDescription(request.description());
        }
        if (request.activityDate() != null) {
            activity.setActivityDate(request.activityDate());
        }
        if (request.location() != null) {
            activity.setLocation(request.location());
        }
        if (request.maxParticipants() != null) {
            activity.setMaxParticipants(request.maxParticipants());
        }
        if (request.registeredCount() != null) {
            activity.setRegisteredCount(request.registeredCount());
        }
        if (request.images() != null) {
            activity.setImages(request.images());
        }
    }

    private ActivityResponse mapToResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getSchool().getId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getActivityDate(),
                activity.getLocation(),
                activity.getMaxParticipants(),
                activity.getRegisteredCount(),
                activity.getImages(),
                activity.getCreatedAt(),
                activity.getUpdatedAt()
        );
    }
}
