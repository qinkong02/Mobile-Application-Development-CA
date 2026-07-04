package com.example.wellness_backend.controller;

import com.example.wellness_backend.dto.ApiResponse;
import com.example.wellness_backend.dto.AuthResponse;
import com.example.wellness_backend.dto.LoginRequest;
import com.example.wellness_backend.dto.RegisterRequest;
import com.example.wellness_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Author: MO YUNDI
 * Controller for user authentication APIs.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ApiResponse.success("User registered successfully", response);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success("Login successful", response);
    }
}