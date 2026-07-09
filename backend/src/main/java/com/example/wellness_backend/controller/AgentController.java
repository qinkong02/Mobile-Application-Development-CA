package com.example.wellness_backend.controller;

import com.example.wellness_backend.dto.RecommendationResponseDTO;
import com.example.wellness_backend.entity.Recommendation;
import com.example.wellness_backend.service.AgentService;
import com.example.wellness_backend.utils.AgentScheduler;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.wellness_backend.dto.ApiResponse;
/**
 * @author Zhang Yuhao
 */
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    @Autowired
    private AgentScheduler agentScheduler;

    /**
     * 手动为指定用户生成建议
     * POST /api/agent/generate/{userId}
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<RecommendationResponseDTO>> generateRecommendation(
            HttpServletRequest request) {
        try {
            Long currentUserId = (Long) request.getAttribute("currentUserId");
            logger.info("收到生成建议请求，userId={}", currentUserId);

            Recommendation recommendation = agentService.generateRecommendationForUser(currentUserId);

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
    @PostMapping("/scheduler/trigger")
    public ResponseEntity<ApiResponse<String>> triggerScheduler() {
        try {
            // 直接调用 Scheduler 的方法
            agentScheduler.generateForAllUsers();
            return ResponseEntity.ok(ApiResponse.success("定时任务已手动触发", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取用户最新的一条建议
     * GET /api/agent/recommendations/latest
     */
    @GetMapping("/recommendations/latest")
    public ResponseEntity<ApiResponse<RecommendationResponseDTO>> getLatestRecommendation(
            HttpServletRequest request) {
        try {
            Long currentUserId = (Long) request.getAttribute("currentUserId");
            logger.info("获取最新建议，userId={}", currentUserId);

            RecommendationResponseDTO dto = agentService.getLatestRecommendation(currentUserId);
            if (dto == null) {
                return ResponseEntity.ok(ApiResponse.success("暂无建议", null));
            }
            return ResponseEntity.ok(ApiResponse.success(dto));

        } catch (Exception e) {
            logger.error("获取最新建议失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}