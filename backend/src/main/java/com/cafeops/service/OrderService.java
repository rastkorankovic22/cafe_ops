package com.cafeops.service;

import com.cafeops.domain.AppUser;
import com.cafeops.domain.CafeOrder;
import com.cafeops.domain.CafeTable;
import com.cafeops.domain.MenuCategory;
import com.cafeops.domain.MenuItem;
import com.cafeops.domain.OrderLine;
import com.cafeops.domain.OrderStatus;
import com.cafeops.domain.Role;
import com.cafeops.domain.Shift;
import com.cafeops.domain.ShiftStatus;
import com.cafeops.repository.AppUserRepository;
import com.cafeops.repository.CafeOrderRepository;
import com.cafeops.repository.CafeTableRepository;
import com.cafeops.repository.MenuCategoryRepository;
import com.cafeops.repository.MenuItemRepository;
import com.cafeops.repository.OrderLineRepository;
import com.cafeops.repository.ShiftRepository;
import com.cafeops.web.dto.AddOrderLineRequest;
import com.cafeops.web.dto.OrderLineResponse;
import com.cafeops.web.dto.OrderResponse;
import com.cafeops.web.dto.UpdateOrderLineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final CafeOrderRepository orderRepository;
    private final OrderLineRepository lineRepository;
    private final CafeTableRepository tableRepository;
    private final ShiftRepository shiftRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final AppUserRepository userRepository;

    public OrderService(
            CafeOrderRepository orderRepository,
            OrderLineRepository lineRepository,
            CafeTableRepository tableRepository,
            ShiftRepository shiftRepository,
            MenuItemRepository menuItemRepository,
            MenuCategoryRepository menuCategoryRepository,
            AppUserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.lineRepository = lineRepository;
        this.tableRepository = tableRepository;
        this.shiftRepository = shiftRepository;
        this.menuItemRepository = menuItemRepository;
        this.menuCategoryRepository = menuCategoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse openOrder(Long userId, Long tableId) {
        AppUser user = requireUser(userId);
        Shift shift = requireOpenShift(user.getCafeId());
        assertCanWorkShift(user, shift);

        CafeTable table = tableRepository.findByIdAndCafeId(tableId, user.getCafeId())
                .filter(CafeTable::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (orderRepository.existsByTableIdAndStatus(table.getId(), OrderStatus.OPEN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Table already has an open order");
        }

        CafeOrder order = new CafeOrder();
        order.setTableId(table.getId());
        order.setShiftId(shift.getId());
        order.setStatus(OrderStatus.OPEN);
        order.setOpenedAt(Instant.now());
        order = orderRepository.save(order);
        return toResponse(order, List.of());
    }

    @Transactional(readOnly = true)
    public OrderResponse currentOrderForTable(Long userId, Long tableId) {
        AppUser user = requireUser(userId);
        tableRepository.findByIdAndCafeId(tableId, user.getCafeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        CafeOrder order = orderRepository.findByTableIdAndStatus(tableId, OrderStatus.OPEN)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return toResponse(order, loadLines(order.getId()));
    }

    @Transactional
    public OrderResponse addLine(Long userId, Long orderId, AddOrderLineRequest request) {
        CafeOrder order = loadOpenOrder(userId, orderId);
        MenuItem item = resolveActiveMenuItem(order, request.menuItemId());

        OrderLine line = new OrderLine();
        line.setOrderId(order.getId());
        line.setMenuItemId(item.getId());
        line.setQuantity(request.quantity());
        line.setUnitPrice(item.getPrice());
        lineRepository.save(line);
        return toResponse(order, loadLines(order.getId()));
    }

    @Transactional
    public OrderResponse updateLine(Long userId, Long orderId, Long lineId, UpdateOrderLineRequest request) {
        CafeOrder order = loadOpenOrder(userId, orderId);
        OrderLine line = lineRepository.findByIdAndOrderId(lineId, order.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        line.setQuantity(request.quantity());
        return toResponse(order, loadLines(order.getId()));
    }

    @Transactional
    public OrderResponse removeLine(Long userId, Long orderId, Long lineId) {
        CafeOrder order = loadOpenOrder(userId, orderId);
        OrderLine line = lineRepository.findByIdAndOrderId(lineId, order.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        lineRepository.delete(line);
        return toResponse(order, loadLines(order.getId()));
    }

    @Transactional
    public OrderResponse closeOrder(Long userId, Long orderId) {
        CafeOrder order = loadOpenOrder(userId, orderId);
        order.setStatus(OrderStatus.CLOSED);
        order.setClosedAt(Instant.now());
        return toResponse(order, loadLines(order.getId()));
    }

    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        AppUser user = requireUser(userId);
        if (user.getRole() != Role.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        CafeOrder order = orderRepository.findById(orderId)
                .filter(o -> o.getStatus() == OrderStatus.OPEN)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        tableRepository.findById(order.getTableId())
                .filter(t -> t.getCafeId().equals(user.getCafeId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        order.setStatus(OrderStatus.CANCELLED);
        order.setClosedAt(Instant.now());
        return toResponse(order, loadLines(order.getId()));
    }

    private CafeOrder loadOpenOrder(Long userId, Long orderId) {
        AppUser user = requireUser(userId);
        CafeOrder order = orderRepository.findByIdAndStatus(orderId, OrderStatus.OPEN)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        tableRepository.findById(order.getTableId())
                .filter(t -> t.getCafeId().equals(user.getCafeId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Shift shift = shiftRepository.findById(order.getShiftId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
        assertCanWorkShift(user, shift);
        return order;
    }

    private MenuItem resolveActiveMenuItem(CafeOrder order, Long menuItemId) {
        Long cafeId = tableRepository.findById(order.getTableId())
                .map(CafeTable::getCafeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        MenuItem item = menuItemRepository.findById(menuItemId)
                .filter(MenuItem::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid menu item"));
        menuCategoryRepository.findById(item.getCategoryId())
                .filter(c -> c.getCafeId().equals(cafeId) && c.isActive())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid menu item"));
        return item;
    }

    private Shift requireOpenShift(Long cafeId) {
        return shiftRepository.findByCafeIdAndStatus(cafeId, ShiftStatus.OPEN)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No open shift"));
    }

    private void assertCanWorkShift(AppUser user, Shift shift) {
        if (user.getRole() == Role.OWNER) {
            return;
        }
        if (user.getRole() == Role.WAITER && user.getId().equals(shift.getWaiterId())) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    private List<OrderLineResponse> loadLines(Long orderId) {
        List<OrderLine> lines = lineRepository.findByOrderIdOrderByIdAsc(orderId);
        List<OrderLineResponse> result = new ArrayList<>();
        for (OrderLine line : lines) {
            String name = menuItemRepository.findById(line.getMenuItemId()).map(MenuItem::getName).orElse("");
            result.add(new OrderLineResponse(
                    line.getId(), line.getMenuItemId(), name, line.getQuantity(), line.getUnitPrice()));
        }
        return result;
    }

    private OrderResponse toResponse(CafeOrder order, List<OrderLineResponse> lines) {
        return new OrderResponse(
                order.getId(),
                order.getTableId(),
                order.getShiftId(),
                order.getStatus(),
                order.getOpenedAt(),
                order.getClosedAt(),
                lines);
    }

    private AppUser requireUser(Long userId) {
        return userRepository.findById(userId)
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}