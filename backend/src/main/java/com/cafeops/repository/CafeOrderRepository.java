package com.cafeops.repository;

import com.cafeops.domain.CafeOrder;
import com.cafeops.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CafeOrderRepository extends JpaRepository<CafeOrder, Long> {

    boolean existsByTableIdAndStatus(Long tableId, OrderStatus status);

    Optional<CafeOrder> findByTableIdAndStatus(Long tableId, OrderStatus status);

    Optional<CafeOrder> findByIdAndStatus(Long id, OrderStatus status);
}