package com.cafeops.web;

import com.cafeops.security.AuthUserPrincipal;
import com.cafeops.service.ShiftService;
import com.cafeops.web.dto.ShiftResponse;
import com.cafeops.web.dto.StartShiftRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('OWNER')")
    public ShiftResponse start(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @Valid @RequestBody StartShiftRequest request
    ) {
        return shiftService.startShift(principal.id(), request);
    }

    @PostMapping("/{id}/end")
    @PreAuthorize("hasRole('OWNER')")
    public ShiftResponse end(@AuthenticationPrincipal AuthUserPrincipal principal, @PathVariable Long id) {
        return shiftService.endShift(principal.id(), id);
    }

    @GetMapping("/current")
    public ShiftResponse current(@AuthenticationPrincipal AuthUserPrincipal principal) {
        return shiftService.currentShift(principal.id());
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public Page<ShiftResponse> history(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return shiftService.history(principal.id(), pageable);
    }
}