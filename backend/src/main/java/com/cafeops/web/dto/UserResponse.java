package com.cafeops.web.dto;

import com.cafeops.domain.Role;

public record UserResponse(Long id, String email, Role role, String fullName, boolean enabled) {
}