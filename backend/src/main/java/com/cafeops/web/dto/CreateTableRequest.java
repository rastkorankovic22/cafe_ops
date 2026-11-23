package com.cafeops.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTableRequest(
        @NotBlank @Size(max = 20) String label,
        int posX,
        int posY
) {
}