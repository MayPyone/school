package com.school.school.pojo;
import java.util.List;
import java.util.UUID;

public record SchoolRequest(
        UUID userId,
        String schoolName,
        String schoolEmail,
        List<String> schoolAddress,
        String logoUrl,
        List<String> phoneNumbers,
        String description,
        String subTitle,
        String customizeSchoolId,
        List<OpeningHourRequest> openingHours
) {}
