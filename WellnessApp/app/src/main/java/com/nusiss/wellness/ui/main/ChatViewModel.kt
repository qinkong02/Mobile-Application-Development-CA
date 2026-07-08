package com.nusiss.wellness.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nusiss.wellness.data.api.RetrofitClient
import com.nusiss.wellness.data.model.ChatMessage
import com.nusiss.wellness.data.model.ChatRequest
import kotlinx.coroutines.launch

/**
 * @author XieMaonan
 */
class ChatViewModel : ViewModel() {

    val messages = mutableListOf<ChatMessage>()
    var historyLoaded = false
        private set
    var isSending = false

    fun loadHistory(onLoaded: () -> Unit) {
        if (historyLoaded) {
            onLoaded()
            return
        }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getChatHistory()
                if (response.isSuccessful) {
                    response.body()?.data?.forEach { item ->
                        messages.add(ChatMessage(item.content, isUser = item.role == "user"))
                    }
                }
            } catch (e: Exception) {
                // 拉取历史失败时静默降级，回退到欢迎语
            }
            if (messages.isEmpty()) {
                messages.add(ChatMessage("Hi! I can help analyze your sleep and exercise data, or answer any health questions.", isUser = false))
            }
            historyLoaded = true
            onLoaded()
        }
    }

    suspend fun sendMessage(text: String): String {
        val response = RetrofitClient.api.sendChatMessage(ChatRequest(text))
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.reply
        } else {
            "Sorry, I can't respond right now. Please try again later."
        }
    }
}
