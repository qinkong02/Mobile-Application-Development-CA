package com.example.wellness_backend.service;
import com.example.wellness_backend.repository.RecommendationRepository;
import com.example.wellness_backend.repository.WellnessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AgentService {
    @Autowired
    private WellnessLogRepository logRepository; // 你们的健康记录DAO

    @Autowired
    private RecommendationRepository recommendationRepository; // 刚建的表
}
