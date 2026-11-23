package com.cafeops.security;

import com.cafeops.domain.Role;

public record AuthUserPrincipal(Long id, String email, Role role) {
}