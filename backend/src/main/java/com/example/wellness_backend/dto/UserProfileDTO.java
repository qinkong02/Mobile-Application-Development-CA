package com.example.wellness_backend.dto;

/**
 * @author XieMaonan
 */
public class UserProfileDTO {
    private Integer heightCm;
    private Double weightKg;
    private Integer age;
    private String gender;

    public UserProfileDTO() {
    }

    public UserProfileDTO(Integer heightCm, Double weightKg, Integer age, String gender) {
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.age = age;
        this.gender = gender;
    }

    public Integer getHeightCm() { return heightCm; }
    public void setHeightCm(Integer heightCm) { this.heightCm = heightCm; }
    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
