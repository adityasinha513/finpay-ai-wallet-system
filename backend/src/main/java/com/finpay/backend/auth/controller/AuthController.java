package com.finpay.backend.auth.controller;

import com.finpay.backend.auth.dto.LoginRequest;
import com.finpay.backend.auth.dto.LoginResponse;
import com.finpay.backend.auth.dto.RegisterRequest;
import com.finpay.backend.auth.dto.RegisterResponse;
import com.finpay.backend.auth.service.AuthService;
import com.finpay.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ApiResponse.ok(
                "Registered",
                authService.register(request)
        );
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ApiResponse.ok(
                "Login successful",
                authService.login(request)
        );
    }
}
