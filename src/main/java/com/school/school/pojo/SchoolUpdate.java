package com.school.school.pojo;

import java.util.List;

public record SchoolUpdate(
        String schoolName,
        String schoolEmail,
        List<String> schoolAddress,
        String logoUrl,
        List<String> phoneNumbers,
        String description,
        String subTitle,
        List<OpeningHourRequest> openingHours
) {}
