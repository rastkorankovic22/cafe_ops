package com.cafeops.web.dto;

public record TableResponse(Long id, String label, int posX, int posY, boolean active, boolean occupied) {
}