package com.cafeops.web;

import com.cafeops.security.AuthUserPrincipal;
import com.cafeops.service.MenuService;
import com.cafeops.web.dto.CreateCategoryRequest;
import com.cafeops.web.dto.CreateMenuItemRequest;
import com.cafeops.web.dto.MenuCategoryResponse;
import com.cafeops.web.dto.MenuItemResponse;
import com.cafeops.web.dto.UpdateCategoryRequest;
import com.cafeops.web.dto.UpdateMenuItemRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<MenuCategoryResponse> getMenu(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        return menuService.getMenu(principal.id(), includeInactive);
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('OWNER')")
    public MenuCategoryResponse createCategory(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return menuService.createCategory(principal.id(), request);
    }

    @PatchMapping("/categories/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public MenuCategoryResponse updateCategory(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        return menuService.updateCategory(principal.id(), id, request);
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('OWNER')")
    public MenuItemResponse createItem(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @Valid @RequestBody CreateMenuItemRequest request
    ) {
        return menuService.createItem(principal.id(), request);
    }

    @PatchMapping("/items/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public MenuItemResponse updateItem(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuItemRequest request
    ) {
        return menuService.updateItem(principal.id(), id, request);
    }
}