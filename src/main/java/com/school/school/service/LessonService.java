package com.school.school.service;

import com.school.school.entity.*;
import com.school.school.pojo.*;
import com.school.school.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LessonService {
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private  final LevelRepository levelRepository;
    private  final UnitRepository unitRepository;

    public LessonService(SchoolRepository schoolRepository, UserRepository userRepository, LessonRepository lessonRepository, LevelRepository levelRepository, UnitRepository unitRepository) {
        this.schoolRepository = schoolRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.levelRepository = levelRepository;
        this.unitRepository = unitRepository;
    }



    public LessonUnitResponse getLesson(UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalStateException((
                        "lesson with id" + lessonId + "does not exist"
                        )));
        List<Unit> units = unitRepository.findByLessonId(lesson.getId());
        return mapToLessonUnitResponse(lesson, units);
    }

    @Transactional
    public LessonResponse createLesson(LessonRequest request, UUID schoolId){
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException(
                        "school with id" + schoolId + "does not exist"
                ));

        User user = userRepository.findById(request.userId())
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
    public LessonResponse updateLesson(LessonUpdate request, UUID lessonId) {
//        School school = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new IllegalStateException(
//                        "school with id" + schoolId + "does not exist"
//                ));
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalStateException(("user not found")));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalStateException("lesson with id"+ lessonId + "does not exist"));

        Level level = levelRepository.findById(request.levelId())
                .orElseThrow(() -> new IllegalStateException(("level not found")));


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

    private LessonUnitResponse mapToLessonUnitResponse(Lesson lesson, List<Unit> units) {
        LessonResponse lessonResponse = mapToResponse(lesson);
        List<UnitResponse> unitResponses = (units == null)
                ? List.of()
                : units.stream()
                .map(this::mapToUnitResponse)
                .toList();

        return new LessonUnitResponse(lessonResponse, unitResponses);
    }

    private UnitResponse mapToUnitResponse(Unit unit) {
        return new UnitResponse(
                unit.getId(),
                unit.getTitle(),
                unit.getContent(),
                unit.getVideoUrl(),
                unit.getLesson() != null ? unit.getLesson().getId().toString() : null,
                unit.getCreatedBy() != null ? unit.getCreatedBy().getId().toString() : null
        );
    }


}
