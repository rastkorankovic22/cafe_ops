package com.cafeops.service;

import com.cafeops.domain.AppUser;
import com.cafeops.domain.Role;
import com.cafeops.repository.AppUserRepository;
import com.cafeops.web.dto.CreateUserRequest;
import com.cafeops.web.dto.UpdateUserRequest;
import com.cafeops.web.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listStaff(Long ownerId) {
        Long cafeId = requireOwnerCafeId(ownerId);
        return userRepository.findByCafeIdOrderByFullNameAsc(cafeId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public UserResponse createWaiter(Long ownerId, CreateUserRequest request) {
        Long cafeId = requireOwnerCafeId(ownerId);
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        AppUser user = new AppUser();
        user.setCafeId(cafeId);
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.WAITER);
        user.setFullName(request.fullName().trim());
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long ownerId, Long userId, UpdateUserRequest request) {
        Long cafeId = requireOwnerCafeId(ownerId);
        AppUser user = userRepository.findById(userId)
                .filter(u -> u.getCafeId().equals(cafeId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (user.getRole() == Role.OWNER && request.enabled() != null && !request.enabled()) {
            long activeOwners = userRepository.countByCafeIdAndRoleAndEnabledIsTrue(cafeId, Role.OWNER);
            if (activeOwners <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot disable the last owner");
            }
        }

        if (user.getId().equals(ownerId) && request.enabled() != null && !request.enabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot disable your own account");
        }

        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return toResponse(user);
    }

    private Long requireOwnerCafeId(Long ownerId) {
        AppUser owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (owner.getRole() != Role.OWNER || !owner.isEnabled()) {
            throw new AccessDeniedException("Owner only");
        }
        return owner.getCafeId();
    }

    private UserResponse toResponse(AppUser user) {
        return new UserResponse(
                user.getId(), user.getEmail(), user.getRole(), user.getFullName(), user.isEnabled());
    }
}