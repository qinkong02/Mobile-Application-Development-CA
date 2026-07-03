package com.example.wellness_backend.controller;

import com.example.wellness_backend.dto.RecommendationResponseDTO;
import com.example.wellness_backend.entity.Recommendation;
import com.example.wellness_backend.service.AgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.wellness_backend.dto.ApiResponse;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    /**
     * POST /api/agent/generate/{userId}
     */
    @PostMapping("/generate/{userId}")
    public ResponseEntity<ApiResponse<RecommendationResponseDTO>> generateRecommendation(
            @PathVariable Long userId) {
        try {
            logger.info("收到生成建议请求，userId={}", userId);

            Recommendation recommendation = agentService.generateRecommendationForUser(userId);

            RecommendationResponseDTO dto = new RecommendationResponseDTO(
                    recommendation.getId(),
                    recommendation.getUser().getId(),
                    recommendation.getRecommendationText(),
                    recommendation.getAvgSleepHours(),
                    recommendation.getTotalExerciseMinutes(),
                    recommendation.getGeneratedAt(),
                    recommendation.getIsRead()
            );

            return ResponseEntity.ok(ApiResponse.success("建议生成成功", dto));

        } catch (RuntimeException e) {
            logger.warn("生成建议失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("生成建议异常", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("系统异常，请稍后重试"));
        }
    }
}