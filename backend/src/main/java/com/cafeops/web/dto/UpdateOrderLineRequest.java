package com.cafeops.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderLineRequest(@NotNull @Min(1) Integer quantity) {
}