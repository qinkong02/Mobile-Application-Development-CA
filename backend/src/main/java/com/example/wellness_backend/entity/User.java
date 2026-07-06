package com.example.wellness_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MO YUNDI / Boliang Wang
 * Entity class for application users.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String role = "USER";

    // profile fields added by XieMaonan：供 chatbot 的 WellnessTools 做个性化建议/热量计算用
    private Integer heightCm;

    private Double weightKg;

    private Integer age;

    private String gender;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WellnessLog> wellnessLogs = new ArrayList<>();

    public User() {
    }

    @PrePersist
    public void setDefaultValues() {
        if (this.role == null) {
            this.role = "USER";
        }
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public List<WellnessLog> getWellnessLogs() {
        return wellnessLogs;
    }

    public Integer getHeightCm() {
        return heightCm;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public Integer getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public void setHeightCm(Integer heightCm) {
        this.heightCm = heightCm;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setWellnessLogs(List<WellnessLog> wellnessLogs) {
        this.wellnessLogs = wellnessLogs;
    }
}