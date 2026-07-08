package com.example.wellness_backend.dto;

import java.time.LocalDateTime;
//返回给前端的agent建议
public class RecommendationResponseDTO {
    private Long id;
    private Long userId;
    private String recommendationText;
    private Double avgSleepHours;
    private Integer totalExerciseMinutes;
    private LocalDateTime generatedAt;
    private Boolean isRead;

    public RecommendationResponseDTO() {}

    public RecommendationResponseDTO(Long id, Long userId, String recommendationText,
                                     Double avgSleepHours, Integer totalExerciseMinutes,
                                     LocalDateTime generatedAt, Boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.recommendationText = recommendationText;
        this.avgSleepHours = avgSleepHours;
        this.totalExerciseMinutes = totalExerciseMinutes;
        this.generatedAt = generatedAt;
        this.isRead = isRead;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getRecommendationText() { return recommendationText; }
    public void setRecommendationText(String recommendationText) { this.recommendationText = recommendationText; }
    public Double getAvgSleepHours() { return avgSleepHours; }
    public void setAvgSleepHours(Double avgSleepHours) { this.avgSleepHours = avgSleepHours; }
    public Integer getTotalExerciseMinutes() { return totalExerciseMinutes; }
    public void setTotalExerciseMinutes(Integer totalExerciseMinutes) { this.totalExerciseMinutes = totalExerciseMinutes; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
}
