package com.cafeops.repository;

import com.cafeops.domain.Shift;
import com.cafeops.domain.ShiftStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    Optional<Shift> findByCafeIdAndStatus(Long cafeId, ShiftStatus status);

    Page<Shift> findByCafeIdOrderByStartedAtDesc(Long cafeId, Pageable pageable);
}