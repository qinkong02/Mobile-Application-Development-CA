package com.example.wellness_backend.service;
import com.example.wellness_backend.dto.HealthRecommendationResponse;
import com.example.wellness_backend.entity.Recommendation;
import com.example.wellness_backend.entity.User;
import com.example.wellness_backend.entity.WellnessLog;
import com.example.wellness_backend.repository.RecommendationRepository;
import com.example.wellness_backend.repository.UserRepository;
import com.example.wellness_backend.repository.WellnessLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgentService {
    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);

    @Autowired
    private WellnessLogRepository wellnessLogRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatClient chatClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Recommendation generateRecommendationForUser(Long userId) throws Exception {
        logger.info("Agent开始为userId={} 生成建议", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        List<WellnessLog> records = wellnessLogRepository
                .findByUserIdAndLogDateAfter(user.getId(), sevenDaysAgo);

        if (records.isEmpty()) {
            throw new RuntimeException("近7天没有健康记录，请先录入数据");
        }
        //User data analysis
        double avgSleep = records.stream()
                .mapToDouble(r -> r.getSleepHours() != null ? r.getSleepHours() : 0)
                .average()
                .orElse(0.0);

        int totalExercise = records.stream()
                .mapToInt(r -> r.getExerciseMinutes() != null ? r.getExerciseMinutes() : 0)
                .sum();

        logger.info("数据分析完成: 平均睡眠={}h, 总运动={}min", avgSleep, totalExercise);

        // Prompt init
        String prompt = buildPrompt(avgSleep, totalExercise, records.size());

        // callAIApi
        HealthRecommendationResponse result = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(HealthRecommendationResponse.class);

        logger.info("AI响应: {}", result);
        List<String> recommendations=result.getRecommendations();
        String summary= result.getSummary();
        String recommendationText = formatRecommendations(recommendations,summary);

        Recommendation recommendation = new Recommendation();
        recommendation.setUser(user);
        recommendation.setRecommendationText(recommendationText);
        recommendation.setAvgSleepHours(avgSleep);
        recommendation.setTotalExerciseMinutes(totalExercise);
        recommendation.setGeneratedAt(LocalDateTime.now());
        recommendation.setIsRead(false);

        Recommendation saved = recommendationRepository.save(recommendation);
        logger.info("建议已保存, id={}", saved.getId());

        return saved;
    }

    private String buildPrompt(double avgSleep, int totalExercise, int recordCount) {
        return String.format(
                "你是一位专业的健康顾问。请根据以下用户最近%d天的健康数据，生成个性化的健康建议。\n\n" +
                        "数据摘要：\n" +
                        "- 平均睡眠时长：%.1f 小时（健康目标：7-8小时）\n" +
                        "- 总运动时长：%d 分钟（健康目标：每周至少150分钟）\n\n" +
                        "请根据以上数据，生成3条具体、可执行、个性化的健康建议。\n" +
                        "建议要具体，比如\"建议每天23:00前睡觉\"而不是\"早点睡\"。\n\n" +
                        "请只返回以下JSON格式，不要有其他内容：\n" +
                        "{{\n" +
                        "    \"recommendations\": [\"建议1\", \"建议2\", \"建议3\"],\n" +
                        "    \"summary\": \"一段简短的总体评价（50字以内）\"\n" +
                        "}}",
                recordCount, avgSleep, totalExercise
        );
    }

    //将AI结构化输出的对象中的建议和总结内容转换为一个字符串
    private String formatRecommendations(List<String> recommendations, String summary) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recommendations.size(); i++) {
            sb.append(i + 1).append(". ").append(recommendations.get(i)).append("\n");
        }
        if (summary != null && !summary.isEmpty()) {
            sb.append("\n【总体评价】").append(summary);
        }
        return sb.toString();
    }

//解析ai返回结果为JSON，已用结构化输出替代
//    private String parseAIResponse(String aiResponse) {
//        try {
//            JsonNode root = objectMapper.readTree(aiResponse);
//            JsonNode recsNode = root.path("recommendations");
//
//            if (recsNode.isArray() && recsNode.size() > 0) {
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < recsNode.size(); i++) {
//                    sb.append(i + 1).append(". ").append(recsNode.get(i).asText()).append("\n");
//                }
//                String summary = root.path("summary").asText();
//                if (!summary.isEmpty()) {
//                    sb.append("\n【总体评价】").append(summary);
//                }
//                return sb.toString();
//            }
//            return aiResponse;
//
//        } catch (Exception e) {
//            logger.warn("解析AI响应失败，使用原始响应", e);
//            return aiResponse;
//        }
//    }

    // 省略 getUserRecommendations、getLatestRecommendation、markAsRead 等方法
    // 这些方法不变，和之前一样
}

