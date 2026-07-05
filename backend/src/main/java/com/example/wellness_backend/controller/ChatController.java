package com.example.wellness_backend.controller;

import com.example.wellness_backend.dto.ApiResponse;
import com.example.wellness_backend.dto.ChatHistoryItemDTO;
import com.example.wellness_backend.dto.ChatRequest;
import com.example.wellness_backend.dto.ChatResponse;
import com.example.wellness_backend.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request, HttpServletRequest httpRequest) {
        try {
            Long currentUserId = (Long) httpRequest.getAttribute("currentUserId");
            String reply = chatService.reply(currentUserId, request.getMessage());
            return ResponseEntity.ok(new ChatResponse(reply));
        } catch (RuntimeException e) {
            logger.warn("聊天请求失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ChatResponse("抱歉，暂时无法回复，请稍后重试"));
        } catch (Exception e) {
            logger.error("聊天请求异常", e);
            return ResponseEntity.internalServerError().body(new ChatResponse("系统异常，请稍后重试"));
        }
    }

    /**
     * GET /api/chat/history
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatHistoryItemDTO>>> history(HttpServletRequest httpRequest) {
        try {
            Long currentUserId = (Long) httpRequest.getAttribute("currentUserId");
            List<ChatHistoryItemDTO> history = chatService.getHistory(currentUserId);
            return ResponseEntity.ok(ApiResponse.success(history));
        } catch (Exception e) {
            logger.error("获取聊天记录异常", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("系统异常，请稍后重试"));
        }
    }
}
