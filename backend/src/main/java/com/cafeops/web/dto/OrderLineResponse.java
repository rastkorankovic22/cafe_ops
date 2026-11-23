package com.cafeops.web.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long id,
        Long menuItemId,
        String menuItemName,
        int quantity,
        BigDecimal unitPrice
) {
}