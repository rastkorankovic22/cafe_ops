package com.cafeops.web;

import com.cafeops.security.AuthUserPrincipal;
import com.cafeops.service.AuthService;
import com.cafeops.web.dto.LoginRequest;
import com.cafeops.web.dto.LoginResponse;
import com.cafeops.web.dto.MeResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.email(), request.password());
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal AuthUserPrincipal principal) {
        return authService.profile(principal.id());
    }
}