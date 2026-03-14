package com.school.school.service;

import com.school.school.entity.Lesson;
import com.school.school.entity.Unit;
import com.school.school.entity.User;
import com.school.school.pojo.CreateUnitRequest;
import com.school.school.pojo.UnitResponse;
import com.school.school.pojo.UpdateUnitRequest;
import com.school.school.repository.LessonRepository;
import com.school.school.repository.UnitRepository;
import com.school.school.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UnitService {
    private LessonRepository lessonRepository;
    private UnitRepository unitRepository;
    private UserRepository userRepository;

    public UnitService(LessonRepository lessonRepository, UnitRepository unitRepository, UserRepository userRepository) {
        this.lessonRepository = lessonRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
    }

    public List<UnitResponse> getUnits (UUID lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalStateException("lesson with id"+ lessonId + "does not exist"));

        List<Unit> units = unitRepository.findByLessonId(lesson.getId());
        return  units.stream().map(this::mapToUnitResponse)
                .toList();
    }

    public UnitResponse getUnitDetail (UUID unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new IllegalStateException("Unit with id"+ unitId + "does not exist"));

        return mapToUnitResponse(unit);

    }

    @Transactional
    public UnitResponse createUnit (CreateUnitRequest request){

        Lesson lesson = lessonRepository.findById(request.lessonId())
                .orElseThrow(() -> new IllegalStateException("lesson with id"+ request.lessonId() + "does not exist"));

        User user = userRepository.findById(request.createdById())
                .orElseThrow(() -> new IllegalStateException("user with id"+ request.createdById() + "does not exist"));

        Unit unit = new Unit();
        if(request.lessonId() !=null){
            unit.setLesson(lesson);
        }
        if(request.createdById() != null) {
            unit.setCreatedBy(user);
        }
        if(request.content() !=null) {
            unit.setContent(request.content());
        }

        if(request.title() != null) {
            unit.setTitle(request.title());
        }

        if(request.videoUrl() != null) {
            unit.setVideoUrl(request.videoUrl());
        }

        return mapToUnitResponse(unitRepository.save(unit));

    }


    @Transactional
    public UnitResponse updateUnit (UpdateUnitRequest request){

        Lesson lesson = lessonRepository.findById(request.lessonId())
                .orElseThrow(() -> new IllegalStateException("lesson with id"+ request.lessonId() + "does not exist"));

        User user = userRepository.findById(request.createdById())
                .orElseThrow(() -> new IllegalStateException("user with id"+ request.createdById() + "does not exist"));

        Unit unit = unitRepository.findById(request.id()).orElseThrow(() -> new IllegalStateException("unit with id"+ request.id() + "doesn not exist"));
        if(request.lessonId() !=null){
            unit.setLesson(lesson);
        }
//        if(request.createdById() != null) {
//            unit.setCreatedBy(user);
//        }
        if(request.content() !=null) {
            unit.setContent(request.content());
        }

        if(request.title() != null) {
            unit.setTitle(request.title());
        }

        if(request.videoUrl() != null) {
            unit.setVideoUrl(request.videoUrl());
        }

        return mapToUnitResponse(unit);

    }

    public void deleteUnit(UUID unitId){
        unitRepository.deleteById(unitId);
    }


    public UnitResponse mapToUnitResponse(Unit unit) {
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
