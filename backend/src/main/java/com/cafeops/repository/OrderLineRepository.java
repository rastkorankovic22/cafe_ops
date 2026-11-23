package com.cafeops.repository;

import com.cafeops.domain.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

    List<OrderLine> findByOrderIdOrderByIdAsc(Long orderId);

    Optional<OrderLine> findByIdAndOrderId(Long id, Long orderId);

    void deleteByOrderId(Long orderId);
}