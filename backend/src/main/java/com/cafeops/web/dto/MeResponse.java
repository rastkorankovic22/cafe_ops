package com.cafeops.web.dto;

import com.cafeops.domain.Role;

public record MeResponse(
        Long id,
        String email,
        Role role,
        String fullName
) {
}