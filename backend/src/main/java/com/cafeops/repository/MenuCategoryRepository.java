package com.cafeops.repository;

import com.cafeops.domain.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    List<MenuCategory> findByCafeIdOrderBySortOrderAscNameAsc(Long cafeId);

    List<MenuCategory> findByCafeIdAndActiveTrueOrderBySortOrderAscNameAsc(Long cafeId);

    Optional<MenuCategory> findByIdAndCafeId(Long id, Long cafeId);
}