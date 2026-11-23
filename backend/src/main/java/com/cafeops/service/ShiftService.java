package com.cafeops.service;

import com.cafeops.domain.AppUser;
import com.cafeops.domain.Role;
import com.cafeops.domain.Shift;
import com.cafeops.domain.ShiftStatus;
import com.cafeops.repository.AppUserRepository;
import com.cafeops.repository.ShiftRepository;
import com.cafeops.web.dto.ShiftResponse;
import com.cafeops.web.dto.StartShiftRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final AppUserRepository userRepository;

    public ShiftService(ShiftRepository shiftRepository, AppUserRepository userRepository) {
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ShiftResponse startShift(Long ownerId, StartShiftRequest request) {
        AppUser owner = requireOwner(ownerId);
        Long cafeId = owner.getCafeId();

        if (shiftRepository.findByCafeIdAndStatus(cafeId, ShiftStatus.OPEN).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A shift is already open");
        }

        AppUser waiter = userRepository.findById(request.waiterId())
                .filter(u -> u.getCafeId().equals(cafeId))
                .filter(u -> u.getRole() == Role.WAITER)
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid waiter"));

        Shift shift = new Shift();
        shift.setCafeId(cafeId);
        shift.setWaiterId(waiter.getId());
        shift.setStatus(ShiftStatus.OPEN);
        shift.setStartedAt(Instant.now());

        try {
            return toResponse(shiftRepository.save(shift), waiter.getFullName());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A shift is already open");
        }
    }

    @Transactional
    public ShiftResponse endShift(Long ownerId, Long shiftId) {
        AppUser owner = requireOwner(ownerId);
        Long cafeId = owner.getCafeId();

        Shift shift = shiftRepository.findById(shiftId)
                .filter(s -> s.getCafeId().equals(cafeId))
                .filter(s -> s.getStatus() == ShiftStatus.OPEN)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        shift.setStatus(ShiftStatus.CLOSED);
        shift.setEndedAt(Instant.now());

        String waiterName = userRepository.findById(shift.getWaiterId())
                .map(AppUser::getFullName)
                .orElse("");
        return toResponse(shift, waiterName);
    }

    @Transactional(readOnly = true)
    public ShiftResponse currentShift(Long userId) {
        AppUser user = userRepository.findById(userId)
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Shift shift = shiftRepository.findByCafeIdAndStatus(user.getCafeId(), ShiftStatus.OPEN)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No open shift"));

        String waiterName = userRepository.findById(shift.getWaiterId())
                .map(AppUser::getFullName)
                .orElse("");
        return toResponse(shift, waiterName);
    }

    @Transactional(readOnly = true)
    public Page<ShiftResponse> history(Long ownerId, Pageable pageable) {
        Long cafeId = requireOwner(ownerId).getCafeId();
        return shiftRepository.findByCafeIdOrderByStartedAtDesc(cafeId, pageable)
                .map(s -> toResponse(s, waiterName(s.getWaiterId())));
    }

    private String waiterName(Long waiterId) {
        return userRepository.findById(waiterId).map(AppUser::getFullName).orElse("");
    }

    private AppUser requireOwner(Long ownerId) {
        return userRepository.findById(ownerId)
                .filter(u -> u.getRole() == Role.OWNER && u.isEnabled())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    private ShiftResponse toResponse(Shift shift, String waiterFullName) {
        return new ShiftResponse(
                shift.getId(),
                shift.getWaiterId(),
                waiterFullName,
                shift.getStatus(),
                shift.getStartedAt(),
                shift.getEndedAt());
    }
}