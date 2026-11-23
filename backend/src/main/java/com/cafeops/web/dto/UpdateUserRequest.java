package com.cafeops.web.dto;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(Boolean enabled, @Size(min = 6, max = 100) String password) {
}