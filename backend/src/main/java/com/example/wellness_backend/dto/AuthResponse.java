package com.example.wellness_backend.dto;

import com.example.wellness_backend.entity.User;
//Author:Mo Yundi
public class AuthResponse {

    private UserInfo user;
    private String token;

    public AuthResponse(User user, String token) {
        this.user = new UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String role;

        public UserInfo(Long id, String username, String email, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}