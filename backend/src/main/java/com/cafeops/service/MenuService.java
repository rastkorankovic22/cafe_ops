package com.cafeops.service;

import com.cafeops.domain.AppUser;
import com.cafeops.domain.MenuCategory;
import com.cafeops.domain.MenuItem;
import com.cafeops.domain.Role;
import com.cafeops.repository.AppUserRepository;
import com.cafeops.repository.MenuCategoryRepository;
import com.cafeops.repository.MenuItemRepository;
import com.cafeops.web.dto.CreateCategoryRequest;
import com.cafeops.web.dto.CreateMenuItemRequest;
import com.cafeops.web.dto.MenuCategoryResponse;
import com.cafeops.web.dto.MenuItemResponse;
import com.cafeops.web.dto.UpdateCategoryRequest;
import com.cafeops.web.dto.UpdateMenuItemRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {

    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository itemRepository;
    private final AppUserRepository userRepository;

    public MenuService(
            MenuCategoryRepository categoryRepository,
            MenuItemRepository itemRepository,
            AppUserRepository userRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuCategoryResponse> getMenu(Long userId, boolean includeInactive) {
        AppUser user = requireUser(userId);
        if (includeInactive && user.getRole() != Role.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        List<MenuCategory> categories = includeInactive
                ? categoryRepository.findByCafeIdOrderBySortOrderAscNameAsc(user.getCafeId())
                : categoryRepository.findByCafeIdAndActiveTrueOrderBySortOrderAscNameAsc(user.getCafeId());

        List<MenuCategoryResponse> result = new ArrayList<>();
        for (MenuCategory category : categories) {
            List<MenuItem> items = includeInactive
                    ? itemRepository.findByCategoryIdOrderByNameAsc(category.getId())
                    : itemRepository.findByCategoryIdAndActiveTrueOrderByNameAsc(category.getId());
            result.add(new MenuCategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getSortOrder(),
                    category.isActive(),
                    items.stream().map(this::toItemResponse).toList()));
        }
        return result;
    }

    @Transactional
    public MenuCategoryResponse createCategory(Long ownerId, CreateCategoryRequest request) {
        Long cafeId = requireOwner(ownerId).getCafeId();
        MenuCategory category = new MenuCategory();
        category.setCafeId(cafeId);
        category.setName(request.name().trim());
        category.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        category.setActive(true);
        category = categoryRepository.save(category);
        return new MenuCategoryResponse(
                category.getId(), category.getName(), category.getSortOrder(), category.isActive(), List.of());
    }

    @Transactional
    public MenuCategoryResponse updateCategory(Long ownerId, Long categoryId, UpdateCategoryRequest request) {
        Long cafeId = requireOwner(ownerId).getCafeId();
        MenuCategory category = categoryRepository.findByIdAndCafeId(categoryId, cafeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (request.name() != null) {
            category.setName(request.name().trim());
        }
        if (request.sortOrder() != null) {
            category.setSortOrder(request.sortOrder());
        }
        if (request.active() != null) {
            category.setActive(request.active());
        }
        List<MenuItemResponse> items = itemRepository.findByCategoryIdOrderByNameAsc(category.getId()).stream()
                .map(this::toItemResponse)
                .toList();
        return new MenuCategoryResponse(
                category.getId(), category.getName(), category.getSortOrder(), category.isActive(), items);
    }

    @Transactional
    public MenuItemResponse createItem(Long ownerId, CreateMenuItemRequest request) {
        Long cafeId = requireOwner(ownerId).getCafeId();
        MenuCategory category = categoryRepository.findByIdAndCafeId(request.categoryId(), cafeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category"));
        MenuItem item = new MenuItem();
        item.setCategoryId(category.getId());
        item.setName(request.name().trim());
        item.setPrice(request.price());
        item.setActive(true);
        return toItemResponse(itemRepository.save(item));
    }

    @Transactional
    public MenuItemResponse updateItem(Long ownerId, Long itemId, UpdateMenuItemRequest request) {
        Long cafeId = requireOwner(ownerId).getCafeId();
        MenuItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        MenuCategory category = categoryRepository.findById(item.getCategoryId())
                .filter(c -> c.getCafeId().equals(cafeId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.categoryId() != null) {
            MenuCategory newCategory = categoryRepository.findByIdAndCafeId(request.categoryId(), cafeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category"));
            item.setCategoryId(newCategory.getId());
        }
        if (request.name() != null) {
            item.setName(request.name().trim());
        }
        if (request.price() != null) {
            item.setPrice(request.price());
        }
        if (request.active() != null) {
            item.setActive(request.active());
        }
        return toItemResponse(item);
    }

    private MenuItemResponse toItemResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(), item.getCategoryId(), item.getName(), item.getPrice(), item.isActive(), item.getImageUrl());
    }

    private AppUser requireUser(Long userId) {
        return userRepository.findById(userId)
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private AppUser requireOwner(Long ownerId) {
        return userRepository.findById(ownerId)
                .filter(u -> u.getRole() == Role.OWNER && u.isEnabled())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
    }
}