package com.nusiss.wellness.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nusiss.wellness.data.api.RetrofitClient
import com.nusiss.wellness.data.model.ChatMessage
import com.nusiss.wellness.data.model.ChatRequest
import com.nusiss.wellness.databinding.FragmentChatBinding
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messages.add(ChatMessage("你好，我可以帮你分析睡眠、运动数据，或者回答健康问题", isUser = false))
        adapter = ChatAdapter(messages)
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChat.adapter = adapter

        binding.btnSend.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        messages.add(ChatMessage(text, isUser = true))
        adapter.notifyItemInserted(messages.size - 1)
        binding.rvChat.scrollToPosition(messages.size - 1)
        binding.etMessage.text?.clear()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.sendChatMessage(ChatRequest(text))
                if (_binding == null) return@launch
                val reply = if (response.isSuccessful && response.body() != null) {
                    response.body()!!.reply
                } else {
                    "抱歉，暂时无法回复，请稍后重试"
                }
                messages.add(ChatMessage(reply, isUser = false))
                adapter.notifyItemInserted(messages.size - 1)
                binding.rvChat.scrollToPosition(messages.size - 1)
            } catch (e: Exception) {
                if (_binding == null) return@launch
                Toast.makeText(requireContext(), "网络连接失败：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
