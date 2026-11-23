package com.cafeops.web.dto;

import jakarta.validation.constraints.NotNull;

public record StartShiftRequest(@NotNull Long waiterId) {
}