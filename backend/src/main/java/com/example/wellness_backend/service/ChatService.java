package com.example.wellness_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author XieMaonan
 */
@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private static final String SYSTEM_PROMPT =
            "你是一位友好的健康助理，可以帮助用户分析睡眠、运动数据，或者回答健康相关的问题。" +
                    "请用简洁、易懂的语言回复。";

    @Autowired
    private ChatClient chatClient;

    public String reply(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("消息不能为空");
        }

        logger.info("收到聊天消息: {}", message);

        String reply = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .call()
                .content();

        logger.info("AI回复: {}", reply);

        return reply;
    }
}
