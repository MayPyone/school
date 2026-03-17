package com.school.school.pojo;

import java.time.LocalTime;

public record OpeningHourRequest(
        String dayOfWeek, // Can be String for flexible JSON parsing, then converted to Enum
        LocalTime openTime,
        LocalTime closeTime
) {}