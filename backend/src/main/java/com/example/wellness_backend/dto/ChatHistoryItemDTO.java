package com.example.wellness_backend.dto;

import java.time.LocalDateTime;

/**
 * @author XieMaonan
 */
public class ChatHistoryItemDTO {
    private String role;
    private String content;
    private LocalDateTime createdAt;

    public ChatHistoryItemDTO() {
    }

    public ChatHistoryItemDTO(String role, String content, LocalDateTime createdAt) {
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
