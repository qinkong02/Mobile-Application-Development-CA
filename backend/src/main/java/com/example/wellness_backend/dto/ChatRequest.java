package com.example.wellness_backend.dto;
/**
 * @author XieMaonan
 */
public class ChatRequest {
    private String message;

    public ChatRequest() {}

    public ChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
