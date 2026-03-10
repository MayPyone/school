package com.school.school.pojo;

import java.util.List;

public record LessonUnitResponse(
        LessonResponse lesson,
        List<UnitResponse> units
) {
}