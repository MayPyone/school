package com.school.school.pojo;
import java.util.List;

public record SchoolRequest(
        String schoolName,
        String schoolEmail,
        String schoolAddress,
        String logoUrl,
        List<String> phoneNumbers,
        String description,
        String subTitle
) {}