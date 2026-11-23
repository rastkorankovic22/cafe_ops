package com.cafeops.repository;

import com.cafeops.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByCategoryIdOrderByNameAsc(Long categoryId);

    List<MenuItem> findByCategoryIdAndActiveTrueOrderByNameAsc(Long categoryId);
}