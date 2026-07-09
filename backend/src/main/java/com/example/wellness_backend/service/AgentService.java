package com.example.wellness_backend.service;
import com.example.wellness_backend.dto.HealthRecommendationResponse;
import com.example.wellness_backend.dto.RecommendationResponseDTO;
import com.example.wellness_backend.entity.Recommendation;
import com.example.wellness_backend.entity.User;
import com.example.wellness_backend.entity.WellnessLog;
import com.example.wellness_backend.repository.RecommendationRepository;
import com.example.wellness_backend.repository.UserRepository;
import com.example.wellness_backend.repository.WellnessLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
//Author:Zhang Yuhao
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
                .findByUser_IdAndLogDateAfter(user.getId(), sevenDaysAgo);
        //获取一周前数据
        LocalDate fourteenDaysAgo = LocalDate.now().minusDays(14);
        List<WellnessLog> lastRecords = wellnessLogRepository
                .findByUserAndLogDateBetween(user.getId(), fourteenDaysAgo, sevenDaysAgo);

        // 计算上周平均值
        double lastAvgSleep = 0.0;
        int lastTotalExercise = 0;
        boolean hasLastWeekData = !lastRecords.isEmpty();
        if (hasLastWeekData) {
            lastAvgSleep = lastRecords.stream()
                    .mapToDouble(r -> r.getSleepHours() != null ? r.getSleepHours() : 0)
                    .average()
                    .orElse(0.0);

            lastTotalExercise = lastRecords.stream()
                    .mapToInt(r -> r.getExerciseMinutes() != null ? r.getExerciseMinutes() : 0)
                    .sum();
        }

        if (records.isEmpty()) {
            throw new RuntimeException("近7天没有健康记录，请先录入数据");
        }
        //计算本周数据
        double currentAvgSleep = records.stream()
                .mapToDouble(r -> r.getSleepHours() != null ? r.getSleepHours() : 0)
                .average()
                .orElse(0.0);

        int currentTotalExercise = records.stream()
                .mapToInt(r -> r.getExerciseMinutes() != null ? r.getExerciseMinutes() : 0)
                .sum();
        // ========== 5. 计算趋势 ==========
        String sleepTrend = calculateTrend(currentAvgSleep, lastAvgSleep, hasLastWeekData);
        String exerciseTrend = calculateTrend(currentTotalExercise, lastTotalExercise, hasLastWeekData);

        logger.info("数据分析完成: 本周睡眠={}h, 上周睡眠={}h, 趋势={}",
                currentAvgSleep, lastAvgSleep, sleepTrend);

        // Prompt init
        String prompt = buildPromptWithHistory(
                currentAvgSleep, currentTotalExercise,
                lastAvgSleep, lastTotalExercise,
                sleepTrend, exerciseTrend,
                hasLastWeekData,
                records.size()
        );

        // callAIApi
//        HealthRecommendationResponse result = chatClient.prompt()
//                .user(prompt)
//                .call()
//                .entity(HealthRecommendationResponse.class);
//
//        logger.info("AI响应: {}", result);
//        List<String> recommendations=result.getRecommendations();
//        String summary= result.getSummary();
//        String recommendationText = formatRecommendations(recommendations,summary);
        // 调用 AI（用 content() 获取原始响应）
        String rawResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        logger.info("=== AI 原始响应: {}", rawResponse);

// 提取 JSON（去掉 Markdown 代码块）
        String jsonStr = extractJson(rawResponse);
        logger.info("=== 提取后的 JSON: {}", jsonStr);

// 手动解析 JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonStr);

        List<String> recommendations = new ArrayList<>();
        JsonNode recsNode = root.get("recommendations");
        if (recsNode.isArray()) {
            for (JsonNode item : recsNode) {
                recommendations.add(item.asText());
            }
        }

        String summary = root.get("summary").asText();
        String recommendationText = formatRecommendations(recommendations, summary);

        Recommendation recommendation = new Recommendation();
        recommendation.setUser(user);
        recommendation.setRecommendationText(recommendationText);
        recommendation.setAvgSleepHours(currentAvgSleep);
        recommendation.setTotalExerciseMinutes(currentTotalExercise);
        recommendation.setGeneratedAt(LocalDateTime.now());
        recommendation.setIsRead(false);

        Recommendation saved = recommendationRepository.save(recommendation);
        logger.info("建议已保存, id={}", saved.getId());

        return saved;
    }

//    private String buildPromptWithHistory(double avgSleep, int totalExercise, int recordCount) {
//        return String.format(
//                "你是一位专业的健康顾问。请根据以下用户最近%d天的健康数据，生成个性化的健康建议。\n\n" +
//                        "数据摘要：\n" +
//                        "- 平均睡眠时长：%.1f 小时（健康目标：7-8小时）\n" +
//                        "- 总运动时长：%d 分钟（健康目标：每周至少150分钟）\n\n" +
//                        "请根据以上数据，生成3条具体、可执行、个性化的健康建议。\n" +
//                        "建议要具体，比如\"建议每天23:00前睡觉\"而不是\"早点睡\"。\n\n" +
//                        "请只返回以下JSON格式，不要有其他内容：\n" +
//                        "{{\n" +
//                        "    \"recommendations\": [\"建议1\", \"建议2\", \"建议3\"],\n" +
//                        "    \"summary\": \"一段简短的总体评价（50字以内）\"\n" +
//                        "}}",
//                recordCount, avgSleep, totalExercise
//        );
//    }
private String buildPromptWithHistory(
        double currentSleep, int currentExercise,
        double lastSleep, int lastExercise,
        String sleepTrend, String exerciseTrend,
        boolean hasLastWeekData,
        int recordCount) {

    StringBuilder prompt = new StringBuilder();
    prompt.append("你是一位专业的健康顾问。请根据以下用户最近").append(recordCount).append("天的健康数据，生成个性化的健康建议。\n\n");

    prompt.append("【本周数据】\n");
    prompt.append("- 平均睡眠时长：").append(String.format("%.1f", currentSleep)).append(" 小时\n");
    prompt.append("- 总运动时长：").append(currentExercise).append(" 分钟\n");

    if (hasLastWeekData) {
        prompt.append("\n【上周数据对比】\n");
        prompt.append("- 上周平均睡眠：").append(String.format("%.1f", lastSleep)).append(" 小时（趋势：").append(sleepTrend).append("）\n");
        prompt.append("- 上周总运动：").append(lastExercise).append(" 分钟（趋势：").append(exerciseTrend).append("）\n");

        // 额外分析
        if (sleepTrend.equals("改善 ↑")) {
            prompt.append("睡眠质量在提升，值得肯定！\n");
        } else if (sleepTrend.equals("下降 ↓")) {
            prompt.append("睡眠有所下降，请重点关注。\n");
        }

        if (exerciseTrend.equals("改善 ↑")) {
            prompt.append("运动量在增加，继续保持！\n");
        } else if (exerciseTrend.equals("下降 ↓")) {
            prompt.append("运动量减少，建议逐步恢复。\n");
        }
    } else {
        prompt.append("\n（暂无上周数据，这是你的第一次分析）\n");
    }

    prompt.append("\n请根据以上数据，生成3条具体、可执行、个性化的健康建议。\n");
    prompt.append("如果趋势显示下降，建议中要包含\"恢复\"或\"调整\"相关的内容。\n");
    prompt.append("如果趋势显示改善，建议中要包含\"继续保持\"相关的内容。\n\n");

    prompt.append("请只返回以下JSON格式，不要有其他内容：\n");
    prompt.append("{\n");
    prompt.append("    \"recommendations\": [\"建议1\", \"建议2\", \"建议3\"],\n");
    prompt.append("    \"summary\": \"总体评价（50字以内，如果趋势改善要表扬，如果下降要鼓励）\"\n");
    prompt.append("}");

    return prompt.toString();
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
    private String calculateTrend(double current, double last, boolean hasLastWeekData) {
        if (!hasLastWeekData) {
            return "无上周数据";
        }
        double diff = current - last;
        if (Math.abs(diff) < 0.1) {
            return "持平";
        }
        return diff > 0 ? "改善" : "下降";
    }

    private String extractJson(String response) {
        String trimmed = response.trim();

        // 去掉 Markdown 代码块
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }

        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }

        return trimmed.trim();
    }

    /**
     * 获取用户最新的一条建议
     */
    public RecommendationResponseDTO getLatestRecommendation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Recommendation rec = recommendationRepository.findTopByUserOrderByGeneratedAtDesc(user);
        if (rec == null) {
            return null;
        }

        return new RecommendationResponseDTO(
                rec.getId(),
                rec.getUser().getId(),
                rec.getRecommendationText(),
                rec.getAvgSleepHours(),
                rec.getTotalExerciseMinutes(),
                rec.getGeneratedAt(),
                rec.getIsRead()
        );
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

