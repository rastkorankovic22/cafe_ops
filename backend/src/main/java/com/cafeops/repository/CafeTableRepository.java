package com.cafeops.repository;

import com.cafeops.domain.CafeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CafeTableRepository extends JpaRepository<CafeTable, Long> {

    List<CafeTable> findByCafeIdAndActiveTrueOrderByLabelAsc(Long cafeId);

    List<CafeTable> findByCafeIdOrderByLabelAsc(Long cafeId);

    Optional<CafeTable> findByIdAndCafeId(Long id, Long cafeId);

    boolean existsByCafeIdAndLabelIgnoreCase(Long cafeId, String label);
}