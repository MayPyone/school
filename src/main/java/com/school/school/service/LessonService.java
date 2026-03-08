package com.school.school.service;

import com.school.school.entity.Lesson;
import com.school.school.entity.Level;
import com.school.school.entity.School;
import com.school.school.entity.User;
import com.school.school.pojo.LessonRequest;
import com.school.school.pojo.LessonResponse;
import com.school.school.pojo.LessonUpdate;
import com.school.school.pojo.SchoolResponse;
import com.school.school.repository.LessonRepository;
import com.school.school.repository.LevelRepository;
import com.school.school.repository.SchoolRepository;
import com.school.school.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LessonService {
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private  final LevelRepository levelRepository;

    public LessonService(SchoolRepository schoolRepository, UserRepository userRepository, LessonRepository lessonRepository, LevelRepository levelRepository) {
        this.schoolRepository = schoolRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.levelRepository = levelRepository;
    }

    @Transactional
    public LessonResponse createLesson(LessonRequest request, UUID schoolId, UUID userId){
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException(
                        "school with id" + schoolId + "does not exist"
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(("user not found")));

        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new IllegalStateException(("level not found")));

        Lesson lesson = new Lesson();
        if(school.getId() !=null){
            lesson.setSchool(school);
        }
        if(user.getId() !=null){
            lesson.setCreatedBy(user);
        }
        if(request.title() !=null){
            lesson.setTitle(request.title());
        }
        if(request.category() !=null){
            lesson.setCategory(request.category());
        }
        if(request.content() !=null){
            lesson.setContent(request.content());
        }
        if(request.levelId() != null){
            lesson.setLevel(level);
        }

      return  mapToResponse(lessonRepository.save(lesson));

    }

    @Transactional
    public LessonResponse updateLesson(LessonUpdate request, UUID schoolId, UUID lessonId, UUID userId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException(
                        "school with id" + schoolId + "does not exist"
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(("user not found")));

        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new IllegalStateException(("level not found")));
        Lesson lesson = new Lesson();

        if(request.title() !=null){
            lesson.setTitle(request.title());
        }
        if(request.category() !=null){
            lesson.setCategory(request.category());
        }
        if(request.content() !=null){
            lesson.setContent(request.content());
        }
        if(request.levelId() != null){
            lesson.setLevel(level);
        }


        return  mapToResponse(lesson);

    }

    private LessonResponse mapToResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getSchool().getId(),
                lesson.getTitle(),
                lesson.getLevel().getName(),
                lesson.getContent(),
                lesson.getCategory(),
                lesson.getCreatedBy().getFirstName() + lesson.getCreatedBy().getLastName()

        );
    }


}
