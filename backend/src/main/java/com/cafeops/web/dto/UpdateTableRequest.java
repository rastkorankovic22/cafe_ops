package com.cafeops.web.dto;

import jakarta.validation.constraints.Size;

public record UpdateTableRequest(@Size(max = 20) String label, Integer posX, Integer posY, Boolean active) {
}