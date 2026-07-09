package com.example.wellness_backend.utils;

import com.example.wellness_backend.entity.User;
import com.example.wellness_backend.repository.UserRepository;
import com.example.wellness_backend.service.AgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
//Author:Zhang Yuhao
@Component
public class AgentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AgentScheduler.class);

    @Autowired
    private AgentService agentService;

    @Autowired
    private UserRepository userRepository;

    // 每周日晚上 22:00 执行
    @Scheduled(cron = "0 0 22 * * SUN")
    //@Scheduled(cron = "0 * * * * *")
    public void generateForAllUsers() {
        logger.info("=== 定时任务：开始为所有用户生成健康建议 ===");

        List<User> users = userRepository.findAll();
        int success = 0;
        int fail = 0;

        for (User user : users) {
            try {
                agentService.generateRecommendationForUser(user.getId());
                success++;
                logger.info("用户 {} 建议生成成功", user.getUsername());
            } catch (Exception e) {
                fail++;
                logger.error("用户 {} 建议生成失败: {}", user.getUsername(), e.getMessage());
            }
        }

        logger.info("=== 定时任务完成：成功 {} 个，失败 {} 个 ===", success, fail);
    }
}