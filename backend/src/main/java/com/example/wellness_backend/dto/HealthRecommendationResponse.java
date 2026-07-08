package com.example.wellness_backend.dto;

import java.util.List;

public class HealthRecommendationResponse {
    private List<String> recommendations;
    private String summary;

    // ⭐ 必须有无参构造函数（Jackson 反序列化需要）
    public HealthRecommendationResponse() {}

    public HealthRecommendationResponse(List<String> recommendations, String summary) {
        this.recommendations = recommendations;
        this.summary = summary;
    }

    // Getters and Setters
    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}