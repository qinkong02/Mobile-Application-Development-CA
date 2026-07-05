package com.example.wellness_backend.repository;

import com.example.wellness_backend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author XieMaonan
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByUser_IdOrderByCreatedAtAsc(Long userId);
}
