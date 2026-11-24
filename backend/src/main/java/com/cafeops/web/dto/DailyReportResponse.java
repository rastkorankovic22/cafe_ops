package com.cafeops.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DailyReportResponse(
        LocalDate date,
        long closedOrders,
        BigDecimal revenue,
        List<TopMenuItemResponse> topItems
) {
}