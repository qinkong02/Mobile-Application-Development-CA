package com.example.wellness_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recommendation_text", columnDefinition = "TEXT")
    private String recommendationText;  // AI生成的建议文本

    @Column(name = "avg_sleep_hours")
    private Double avgSleepHours;  // 生成建议时的平均睡眠（记录快照）

    @Column(name = "total_exercise_minutes")
    private Integer totalExerciseMinutes;  // 生成建议时的总运动量（快照）

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;  // 生成时间

    @Column(name = "is_read")
    private Boolean isRead = false;  // 用户是否已读

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
