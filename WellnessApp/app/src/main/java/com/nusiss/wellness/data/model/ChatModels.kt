package com.nusiss.wellness.data.model

data class ChatRequest(val message: String)
data class ChatResponse(val reply: String)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isLoading: Boolean = false
)

// added by XieMaonan：对应 GET /api/chat/history 的返回项，用于在聊天记录丢失的
// bug 修复中，把后端持久化的历史消息映射回前端展示用的 ChatMessage。
data class ChatHistoryItem(val role: String, val content: String, val createdAt: String?)
