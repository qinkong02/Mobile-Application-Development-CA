package com.example.wellness_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Author: Boliang Wang
 * This class stores daily wellness records.
 */
@Entity
@Table(name = "wellness_logs")
public class WellnessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDate logDate;

    private Double sleepHours;

    private String exerciseType;

    private Integer exerciseMinutes;

    private Integer moodScore;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public WellnessLog() {
    }

    @PrePersist
    public void createTime() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (userId == null) {
            userId = 1L;
        }

        if (logDate == null) {
            logDate = LocalDate.now();
        }
    }

    @PreUpdate
    public void updateTime() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public Double getSleepHours() {
        return sleepHours;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public Integer getExerciseMinutes() {
        return exerciseMinutes;
    }

    public Integer getMoodScore() {
        return moodScore;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public void setSleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public void setExerciseMinutes(Integer exerciseMinutes) {
        this.exerciseMinutes = exerciseMinutes;
    }

    public void setMoodScore(Integer moodScore) {
        this.moodScore = moodScore;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}