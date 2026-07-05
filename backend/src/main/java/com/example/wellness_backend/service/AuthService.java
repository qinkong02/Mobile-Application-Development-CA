package com.example.wellness_backend.service;

import com.example.wellness_backend.dto.AuthResponse;
import com.example.wellness_backend.dto.LoginRequest;
import com.example.wellness_backend.dto.RegisterRequest;
import com.example.wellness_backend.entity.User;
import com.example.wellness_backend.repository.UserRepository;
import com.example.wellness_backend.utils.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        return new AuthResponse(savedUser, jwtUtils.generateToken(savedUser.getId()));
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return new AuthResponse(user, jwtUtils.generateToken(user.getId()));
    }
}