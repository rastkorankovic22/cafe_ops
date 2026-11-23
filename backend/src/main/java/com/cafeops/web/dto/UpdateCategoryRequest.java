package com.cafeops.web.dto;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(@Size(max = 80) String name, Integer sortOrder, Boolean active) {
}