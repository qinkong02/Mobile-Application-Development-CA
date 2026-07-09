package com.example.wellness_backend.service;

import com.example.wellness_backend.dto.ChatHistoryItemDTO;
import com.example.wellness_backend.entity.ChatMessage;
import com.example.wellness_backend.entity.User;
import com.example.wellness_backend.repository.ChatMessageRepository;
import com.example.wellness_backend.repository.UserRepository;
import com.example.wellness_backend.utils.WellnessTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author XieMaonan/ZhengChaorui
 */
@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private static final String SYSTEM_PROMPT =
            "你是一位友好的健康助理，可以帮助用户分析睡眠、运动数据，或者回答健康相关的问题。" +
                    "如果用户的问题涉及个性化建议（比如减肥、饮食、运动计划），" +
                    "请调用可用的工具获取用户的身高体重年龄性别、近期健康数据或计算所需的热量，" +
                    "不要凭空猜测或编造这些数值。请用简洁、易懂的语言回复。";

    /** 传给 AI 的最近历史消息条数上限，避免 token 过多 */
    private static final int MAX_CONTEXT_MESSAGES = 20;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    // added by XieMaonan：让 ChatClient 按需调用工具查询用户资料/健康数据，
    // 而不是每次都把这些数据静态拼进 prompt
    @Autowired
    private WellnessTools wellnessTools;

    public String reply(Long userId, String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("消息不能为空");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        logger.info("收到聊天消息: {}", message);

        List<Message> contextMessages = buildContextMessages(userId, message);
        logger.info("多轮上下文: 携带 {} 条历史消息", contextMessages.size() - 1);

        String reply = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .messages(contextMessages)
                .tools(wellnessTools)
                .toolContext(Map.of("userId", userId))
                .call()
                .content();

        logger.info("AI回复: {}", reply);

        chatMessageRepository.save(new ChatMessage(user, "user", message));
        chatMessageRepository.save(new ChatMessage(user, "bot", reply));

        return reply;
    }

    private List<Message> buildContextMessages(Long userId, String currentMessage) {
        List<ChatMessage> history = chatMessageRepository.findByUser_IdOrderByCreatedAtAsc(userId);
        int start = Math.max(0, history.size() - MAX_CONTEXT_MESSAGES);

        List<Message> messages = new ArrayList<>();
        for (int i = start; i < history.size(); i++) {
            ChatMessage item = history.get(i);
            if ("user".equals(item.getRole())) {
                messages.add(new UserMessage(item.getContent()));
            } else if ("bot".equals(item.getRole())) {
                messages.add(new AssistantMessage(item.getContent()));
            }
        }
        messages.add(new UserMessage(currentMessage));
        return messages;
    }

    public List<ChatHistoryItemDTO> getHistory(Long userId) {
        return chatMessageRepository.findByUser_IdOrderByCreatedAtAsc(userId).stream()
                .map(m -> new ChatHistoryItemDTO(m.getRole(), m.getContent(), m.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
