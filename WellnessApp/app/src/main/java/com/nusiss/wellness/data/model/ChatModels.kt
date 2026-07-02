package com.nusiss.wellness.data.model

data class ChatRequest(val message: String)
data class ChatResponse(val reply: String)

data class ChatMessage(val text: String, val isUser: Boolean)
