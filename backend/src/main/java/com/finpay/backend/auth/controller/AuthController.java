package com.finpay.backend.auth.controller;

import com.finpay.backend.auth.dto.LoginRequest;
import com.finpay.backend.auth.dto.LoginResponse;
import com.finpay.backend.auth.dto.RegisterRequest;
import com.finpay.backend.auth.dto.RegisterResponse;
import com.finpay.backend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }
}