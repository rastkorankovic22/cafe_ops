package com.cafeops.repository;

import com.cafeops.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailIgnoreCase(String email);

    List<AppUser> findByCafeIdOrderByFullNameAsc(Long cafeId);

    boolean existsByEmailIgnoreCase(String email);

    long countByCafeIdAndRoleAndEnabledIsTrue(Long cafeId, com.cafeops.domain.Role role);
}