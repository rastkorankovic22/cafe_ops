package com.cafeops.web.dto;

import com.cafeops.domain.Role;

public record LoginResponse(
        String accessToken,
        Long userId,
        String email,
        Role role,
        String fullName
) {
}