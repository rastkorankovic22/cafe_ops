package com.cafeops.web.dto;

import java.math.BigDecimal;

public record MenuItemResponse(Long id, Long categoryId, String name, BigDecimal price, boolean active) {
}