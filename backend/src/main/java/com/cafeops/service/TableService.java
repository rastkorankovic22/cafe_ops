package com.cafeops.service;

import com.cafeops.domain.AppUser;
import com.cafeops.domain.CafeTable;
import com.cafeops.domain.OrderStatus;
import com.cafeops.domain.Role;
import com.cafeops.repository.AppUserRepository;
import com.cafeops.repository.CafeOrderRepository;
import com.cafeops.repository.CafeTableRepository;
import com.cafeops.web.dto.CreateTableRequest;
import com.cafeops.web.dto.TableResponse;
import com.cafeops.web.dto.UpdateTableRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TableService {

    private final CafeTableRepository tableRepository;
    private final CafeOrderRepository orderRepository;
    private final AppUserRepository userRepository;

    public TableService(
            CafeTableRepository tableRepository,
            CafeOrderRepository orderRepository,
            AppUserRepository userRepository
    ) {
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TableResponse> list(Long userId, boolean includeInactive) {
        AppUser user = requireUser(userId);
        if (includeInactive && user.getRole() != Role.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        List<CafeTable> tables = includeInactive
                ? tableRepository.findByCafeIdOrderByLabelAsc(user.getCafeId())
                : tableRepository.findByCafeIdAndActiveTrueOrderByLabelAsc(user.getCafeId());
        return tables.stream().map(this::toResponse).toList();
    }

    @Transactional
    public TableResponse create(Long ownerId, CreateTableRequest request) {
        Long cafeId = requireOwner(ownerId).getCafeId();
        String label = request.label().trim();
        if (tableRepository.existsByCafeIdAndLabelIgnoreCase(cafeId, label)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Table label already exists");
        }
        CafeTable table = new CafeTable();
        table.setCafeId(cafeId);
        table.setLabel(label);
        table.setPosX(request.posX());
        table.setPosY(request.posY());
        table.setActive(true);
        return toResponse(tableRepository.save(table));
    }

    @Transactional
    public TableResponse update(Long ownerId, Long tableId, UpdateTableRequest request) {
        Long cafeId = requireOwner(ownerId).getCafeId();
        CafeTable table = tableRepository.findByIdAndCafeId(tableId, cafeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.label() != null) {
            String label = request.label().trim();
            if (!label.equalsIgnoreCase(table.getLabel())
                    && tableRepository.existsByCafeIdAndLabelIgnoreCase(cafeId, label)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Table label already exists");
            }
            table.setLabel(label);
        }
        if (request.posX() != null) {
            table.setPosX(request.posX());
        }
        if (request.posY() != null) {
            table.setPosY(request.posY());
        }
        if (request.active() != null) {
            if (!request.active() && orderRepository.existsByTableIdAndStatus(table.getId(), OrderStatus.OPEN)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Table has an open order");
            }
            table.setActive(request.active());
        }
        return toResponse(table);
    }

    @Transactional
    public void remove(Long ownerId, Long tableId) {
        Long cafeId = requireOwner(ownerId).getCafeId();
        CafeTable table = tableRepository.findByIdAndCafeId(tableId, cafeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (orderRepository.existsByTableIdAndStatus(table.getId(), OrderStatus.OPEN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Table has an open order");
        }
        table.setActive(false);
    }

    private TableResponse toResponse(CafeTable table) {
        boolean occupied = orderRepository.existsByTableIdAndStatus(table.getId(), OrderStatus.OPEN);
        return new TableResponse(
                table.getId(), table.getLabel(), table.getPosX(), table.getPosY(), table.isActive(), occupied);
    }

    private AppUser requireUser(Long userId) {
        return userRepository.findById(userId)
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private AppUser requireOwner(Long ownerId) {
        return userRepository.findById(ownerId)
                .filter(u -> u.getRole() == Role.OWNER && u.isEnabled())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
    }
}