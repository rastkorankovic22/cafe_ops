package com.cafeops.web;

import com.cafeops.security.AuthUserPrincipal;
import com.cafeops.service.UserService;
import com.cafeops.web.dto.CreateUserRequest;
import com.cafeops.web.dto.UpdateUserRequest;
import com.cafeops.web.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('OWNER')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> list(@AuthenticationPrincipal AuthUserPrincipal principal) {
        return userService.listStaff(principal.id());
    }

    @PostMapping
    public UserResponse create(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @Valid @RequestBody CreateUserRequest request
    ) {
        return userService.createWaiter(principal.id(), request);
    }

    @PatchMapping("/{id}")
    public UserResponse update(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return userService.updateUser(principal.id(), id, request);
    }
}