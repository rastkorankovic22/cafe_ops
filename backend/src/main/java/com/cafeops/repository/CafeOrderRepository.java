package com.cafeops.repository;

import com.cafeops.domain.CafeOrder;
import com.cafeops.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeOrderRepository extends JpaRepository<CafeOrder, Long> {

    boolean existsByTableIdAndStatus(Long tableId, OrderStatus status);
}