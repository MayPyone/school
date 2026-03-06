package com.school.school.pojo;
import java.util.List;
import java.util.UUID;

public record SchoolResponse(
        UUID id,
        String schoolName,
        String schoolEmail,
        List<String>  schoolAddress,
        String logoUrl,
        List<String> phoneNumbers,
        String description,
        String subTitle
) {}