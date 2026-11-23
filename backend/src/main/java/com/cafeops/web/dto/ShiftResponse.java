package com.cafeops.web.dto;

import com.cafeops.domain.ShiftStatus;

import java.time.Instant;

public record ShiftResponse(
        Long id,
        Long waiterId,
        String waiterFullName,
        ShiftStatus status,
        Instant startedAt,
        Instant endedAt
) {
}