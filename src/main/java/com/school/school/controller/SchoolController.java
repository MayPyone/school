package com.school.school.controller;
import com.school.school.pojo.SchoolRequest;
import com.school.school.pojo.SchoolResponse;
import com.school.school.pojo.SchoolUpdate;
import com.school.school.pojo.ActivityResponse;
import com.school.school.pojo.ClassScheduleResponse;
import com.school.school.pojo.StaffResponse;
import com.school.school.service.ActivityService;
import com.school.school.service.ClassScheduleService;
import com.school.school.service.SchoolService;
import com.school.school.service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schools")
public class SchoolController {
    private final SchoolService schoolService;
    private final ClassScheduleService classScheduleService;
    private final StaffService staffService;
    private final ActivityService activityService;

    public SchoolController(SchoolService schoolService, ClassScheduleService classScheduleService, StaffService staffService, ActivityService activityService) {
        this.schoolService = schoolService;
        this.classScheduleService = classScheduleService;
        this.staffService = staffService;
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<SchoolResponse> addSchool(@RequestBody SchoolRequest request) {
        return new ResponseEntity<>(schoolService.createSchool(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SchoolResponse>> listSchools() {
        return ResponseEntity.ok(schoolService.getAllSchools());
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<SchoolResponse> getSchool(@PathVariable("id") String id) {
        return ResponseEntity.ok(schoolService.getSchool(id));
    }

    @GetMapping(path = "{id}/schedules")
    public ResponseEntity<List<ClassScheduleResponse>> getSchoolSchedules(@PathVariable("id") String id) {
        return ResponseEntity.ok(classScheduleService.getSchedules(id));
    }

    @GetMapping(path = "{id}/staff")
    public ResponseEntity<List<StaffResponse>> getSchoolStaff(@PathVariable("id") String id) {
        return ResponseEntity.ok(staffService.getActiveStaff(id));
    }

    @GetMapping(path = "{id}/activities")
    public ResponseEntity<List<ActivityResponse>> getSchoolActivities(@PathVariable("id") String id) {
        return ResponseEntity.ok(activityService.getActivities(id));
    }

    @PutMapping(path= "{id}")
    public ResponseEntity<SchoolResponse> updateSchool (@PathVariable("id") UUID id,@RequestBody SchoolUpdate request) {
        return ResponseEntity.ok(schoolService.updateSchool(request, id));
    }

    @DeleteMapping(path = "{id}")
    public  void deleteSchool (@PathVariable ("id") UUID id) {
          schoolService.deleteSchool(id);
    }
}
