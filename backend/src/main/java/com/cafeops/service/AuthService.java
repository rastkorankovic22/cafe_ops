package com.cafeops.service;

import com.cafeops.domain.AppUser;
import com.cafeops.repository.AppUserRepository;
import com.cafeops.security.JwtService;
import com.cafeops.web.dto.LoginResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(String email, String password) {
        AppUser user = userRepository.findByEmailIgnoreCase(email)
                .filter(AppUser::isEnabled)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.createToken(user.getId(), user.getEmail(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getEmail(), user.getRole(), user.getFullName());
    }

    public com.cafeops.web.dto.MeResponse profile(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("Invalid token"));
        return new com.cafeops.web.dto.MeResponse(user.getId(), user.getEmail(), user.getRole(), user.getFullName());
    }
}