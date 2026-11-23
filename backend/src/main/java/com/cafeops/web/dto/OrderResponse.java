package com.cafeops.web.dto;

import com.cafeops.domain.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long tableId,
        Long shiftId,
        OrderStatus status,
        Instant openedAt,
        Instant closedAt,
        List<OrderLineResponse> lines
) {
}