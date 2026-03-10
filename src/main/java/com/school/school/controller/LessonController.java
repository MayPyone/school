package com.school.school.controller;

import com.school.school.pojo.LessonRequest;
import com.school.school.pojo.LessonResponse;
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



   @PostMapping
    public LessonResponse createLesson(@PathVariable UUID schoolId, @RequestParam UUID userId, @RequestBody  LessonRequest request){
        return lessonService.createLesson(request, schoolId, userId);
   }
}
