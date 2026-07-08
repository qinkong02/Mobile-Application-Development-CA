package com.example.wellness_backend.tool;

import com.example.wellness_backend.entity.User;
import com.example.wellness_backend.entity.WellnessLog;
import com.example.wellness_backend.repository.UserRepository;
import com.example.wellness_backend.repository.WellnessLogRepository;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * @author XieMaonan
 * 提供给 ChatClient 的工具方法：模型按需调用这些方法获取用户的静态资料和近期健康数据，
 * 而不是每次都把这些信息硬拼进 system prompt。userId 通过 ToolContext 由后端注入，
 * 不经过模型传参，避免模型伪造/猜测其他用户的 id。
 */
@Component
public class WellnessTools {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WellnessLogRepository wellnessLogRepository;

    @Tool(description = "获取当前用户的身高(cm)、体重(kg)、年龄、性别等基本信息，用于给出针对性的健康或饮食建议")
    public String getUserProfile(ToolContext toolContext) {
        User user = userRepository.findById(getUserId(toolContext)).orElse(null);
        if (user == null) {
            return "用户信息不存在";
        }
        if (user.getHeightCm() == null && user.getWeightKg() == null
                && user.getAge() == null && user.getGender() == null) {
            return "用户还没有填写身高、体重、年龄、性别等信息，可以礼貌地询问用户，获取后再给出针对性建议";
        }
        return String.format("身高: %s cm, 体重: %s kg, 年龄: %s 岁, 性别: %s",
                user.getHeightCm(), user.getWeightKg(), user.getAge(), user.getGender());
    }

    @Tool(description = "获取当前用户最近7天的睡眠和运动数据统计，包括平均睡眠时长和总运动时长")
    public String getRecentWellnessSummary(ToolContext toolContext) {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        List<WellnessLog> records = wellnessLogRepository
                .findByUser_IdAndLogDateAfter(getUserId(toolContext), sevenDaysAgo);

        if (records.isEmpty()) {
            return "用户最近7天没有健康记录";
        }

        double avgSleep = records.stream()
                .mapToDouble(r -> r.getSleepHours() != null ? r.getSleepHours() : 0)
                .average()
                .orElse(0.0);
        int totalExercise = records.stream()
                .mapToInt(r -> r.getExerciseMinutes() != null ? r.getExerciseMinutes() : 0)
                .sum();

        return String.format("最近7天共 %d 条记录，平均睡眠 %.1f 小时/天，总运动时长 %d 分钟",
                records.size(), avgSleep, totalExercise);
    }

    @Tool(description = "根据用户的身高、体重、年龄、性别计算基础代谢率(BMR)和每日推荐热量摄入(TDEE)，"
            + "用于减肥或饮食建议场景；请调用此工具获取准确数值，不要自己估算")
    public String calculateCalorieNeeds(ToolContext toolContext) {
        User user = userRepository.findById(getUserId(toolContext)).orElse(null);
        if (user == null || user.getHeightCm() == null || user.getWeightKg() == null
                || user.getAge() == null || user.getGender() == null) {
            return "缺少身高、体重、年龄或性别信息，无法计算，请先询问用户补充";
        }

        double bmr = "MALE".equalsIgnoreCase(user.getGender())
                ? 10 * user.getWeightKg() + 6.25 * user.getHeightCm() - 5 * user.getAge() + 5
                : 10 * user.getWeightKg() + 6.25 * user.getHeightCm() - 5 * user.getAge() - 161;
        double tdee = bmr * 1.375; // 默认按轻度活动量估算

        return String.format("基础代谢率(BMR) 约 %.0f 千卡/天，日常轻度活动下建议摄入热量(TDEE) 约 %.0f 千卡/天",
                bmr, tdee);
    }

    private Long getUserId(ToolContext toolContext) {
        return (Long) toolContext.getContext().get("userId");
    }
}
