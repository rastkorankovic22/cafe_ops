package com.cafeops.web;

import com.cafeops.security.AuthUserPrincipal;
import com.cafeops.service.OrderService;
import com.cafeops.web.dto.AddOrderLineRequest;
import com.cafeops.web.dto.OrderResponse;
import com.cafeops.web.dto.UpdateOrderLineRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/tables/{tableId}/orders")
    public OrderResponse open(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long tableId
    ) {
        return orderService.openOrder(principal.id(), tableId);
    }

    @GetMapping("/tables/{tableId}/orders/current")
    public OrderResponse currentForTable(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long tableId
    ) {
        return orderService.currentOrderForTable(principal.id(), tableId);
    }

    @PostMapping("/orders/{orderId}/lines")
    public OrderResponse addLine(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long orderId,
            @Valid @RequestBody AddOrderLineRequest request
    ) {
        return orderService.addLine(principal.id(), orderId, request);
    }

    @PatchMapping("/orders/{orderId}/lines/{lineId}")
    public OrderResponse updateLine(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long orderId,
            @PathVariable Long lineId,
            @Valid @RequestBody UpdateOrderLineRequest request
    ) {
        return orderService.updateLine(principal.id(), orderId, lineId, request);
    }

    @DeleteMapping("/orders/{orderId}/lines/{lineId}")
    public OrderResponse removeLine(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long orderId,
            @PathVariable Long lineId
    ) {
        return orderService.removeLine(principal.id(), orderId, lineId);
    }

    @PostMapping("/orders/{orderId}/close")
    public OrderResponse close(@AuthenticationPrincipal AuthUserPrincipal principal, @PathVariable Long orderId) {
        return orderService.closeOrder(principal.id(), orderId);
    }

    @PostMapping("/orders/{orderId}/cancel")
    @PreAuthorize("hasRole('OWNER')")
    public OrderResponse cancel(@AuthenticationPrincipal AuthUserPrincipal principal, @PathVariable Long orderId) {
        return orderService.cancelOrder(principal.id(), orderId);
    }
}