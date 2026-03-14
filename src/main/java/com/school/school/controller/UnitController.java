package com.school.school.controller;

import com.school.school.pojo.CreateUnitRequest;
import com.school.school.pojo.UnitResponse;
import com.school.school.pojo.UpdateUnitRequest;
import com.school.school.service.LessonService;
import com.school.school.service.UnitService;
import com.school.school.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lessons/{lessonId}/units")
public class UnitController {
    private final LessonService lessonService;
    private final UnitService unitService;
    private final UserService userService;

    public UnitController(LessonService lessonService, UnitService unitService, UserService userService) {
        this.lessonService = lessonService;
        this.unitService = unitService;
        this.userService = userService;
    }

    @GetMapping
    public List<UnitResponse> getAllUnits (@PathVariable UUID lessonId){
        return unitService.getUnits(lessonId);
    }

    @GetMapping("{unitId}")
    public UnitResponse getUnit (@PathVariable UUID unitId) {
        return unitService.getUnitDetail(unitId);
    }

    @PostMapping
    public UnitResponse createUnit (@RequestBody CreateUnitRequest request){
        return unitService.createUnit(request);
    }

    @PutMapping("{unitId}")
    public UnitResponse updateUnit (@RequestBody UpdateUnitRequest request) {
        return unitService.updateUnit(request);
    }

    @DeleteMapping("{unitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUnit (@PathVariable UUID unitId){
        unitService.deleteUnit(unitId);
    }
}
