package com.example.wellness_backend.service;

import com.example.wellness_backend.dto.AuthResponse;
import com.example.wellness_backend.dto.LoginRequest;
import com.example.wellness_backend.dto.RegisterRequest;
import com.example.wellness_backend.entity.User;
import com.example.wellness_backend.repository.UserRepository;
import com.example.wellness_backend.utils.JwtUtils;
import org.springframework.stereotype.Service;
//Author:Mo Yundi
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse register(RegisterRequest request) {

        String username = request.getUsername().trim();
        String email = request.getEmail().trim();
        String password = request.getPassword();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        String token = jwtUtils.generateToken(savedUser.getId());

        return new AuthResponse(savedUser, token);
    }

    public AuthResponse login(LoginRequest request) {

        String username = request.getUsername().trim();
        String password = request.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username does not exist"));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Password is incorrect");
        }

        String token = jwtUtils.generateToken(user.getId());

        return new AuthResponse(user, token);
    }
}