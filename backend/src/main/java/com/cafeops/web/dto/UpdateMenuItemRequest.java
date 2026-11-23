package com.cafeops.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateMenuItemRequest(
        Long categoryId,
        @Size(max = 120) String name,
        @DecimalMin("0") BigDecimal price,
        Boolean active
) {
}