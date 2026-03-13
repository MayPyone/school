package com.school.school.controller;

import com.school.school.pojo.LessonRequest;
import com.school.school.pojo.LessonResponse;
import com.school.school.pojo.LessonUnitResponse;
import com.school.school.pojo.LessonUpdate;
import com.school.school.service.LessonService;
import com.school.school.service.SchoolService;
import com.school.school.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/schools/{schoolId}/lessons")
public class LessonController {
    private final SchoolService schoolService;
    private final LessonService lessonService;
    private final UserService userService;

    public LessonController(SchoolService schoolService, LessonService lessonService, UserService userService) {
        this.schoolService = schoolService;
        this.lessonService = lessonService;
        this.userService = userService;
    }

    @GetMapping("/{lessonId}")
    public LessonUnitResponse getLessons(@PathVariable UUID lessonId){
        return lessonService.getLesson(lessonId);
    }

   @PostMapping
    public LessonResponse createLesson(@PathVariable UUID schoolId, @RequestBody  LessonRequest request){
        return lessonService.createLesson(request, schoolId);
   }

   @PutMapping("/{lessonId}")
    public LessonResponse updateLesson(@RequestBody LessonUpdate request, @PathVariable UUID lessonId){
        return  lessonService.updateLesson(request, lessonId);
   }
}
