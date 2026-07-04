package com.example.wellness_backend.controller;

import com.example.wellness_backend.dto.ChatRequest;
import com.example.wellness_backend.dto.ChatResponse;
import com.example.wellness_backend.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XieMaonan
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    /**
     * POST /api/chat
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            String reply = chatService.reply(request.getMessage());
            return ResponseEntity.ok(new ChatResponse(reply));
        } catch (RuntimeException e) {
            logger.warn("聊天请求失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ChatResponse("抱歉，暂时无法回复，请稍后重试"));
        } catch (Exception e) {
            logger.error("聊天请求异常", e);
            return ResponseEntity.internalServerError().body(new ChatResponse("系统异常，请稍后重试"));
        }
    }
}
