package com.cafeops.web.dto;

import java.util.List;

public record MenuCategoryResponse(
        Long id,
        String name,
        int sortOrder,
        boolean active,
        List<MenuItemResponse> items
) {
}