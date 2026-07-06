package com.nusiss.wellness.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nusiss.wellness.data.model.ChatMessage
import com.nusiss.wellness.databinding.FragmentChatBinding
import kotlinx.coroutines.launch

// bug fix by XieMaonan：原实现把 messages 列表存成 ChatFragment 的本地成员变量，
// 而 MainActivity 切换底部导航时是用 replace() 整个销毁重建 Fragment 的，
// 导致每次切走再切回聊天页，本地变量被清空、聊天记录全部消失。
// 这里改为从 ChatViewModel 读取消息列表——ViewModel 的生命周期跨越 Fragment
// 销毁重建，配合后端的历史记录接口即可修复该问题。
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatAdapter(viewModel.messages)
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChat.adapter = adapter

        if (viewModel.historyLoaded) {
            adapter.notifyDataSetChanged()
            scrollToBottom()
        } else {
            viewModel.loadHistory {
                if (_binding == null) return@loadHistory
                adapter.notifyDataSetChanged()
                scrollToBottom()
            }
        }

        binding.btnSend.setOnClickListener { sendMessage() }
        setSending(viewModel.isSending)
    }

    private fun scrollToBottom() {
        if (viewModel.messages.isNotEmpty()) {
            binding.rvChat.scrollToPosition(viewModel.messages.size - 1)
        }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty() || viewModel.isSending) return

        viewModel.messages.add(ChatMessage(text, isUser = true))
        adapter.notifyItemInserted(viewModel.messages.size - 1)

        val loadingIndex = viewModel.messages.size
        viewModel.messages.add(ChatMessage("", isUser = false, isLoading = true))
        adapter.notifyItemInserted(loadingIndex)

        scrollToBottom()
        binding.etMessage.text?.clear()
        setSending(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val reply = viewModel.sendMessage(text)
                if (_binding == null) return@launch
                removeLoadingMessage(loadingIndex)
                viewModel.messages.add(ChatMessage(reply, isUser = false))
                adapter.notifyItemInserted(viewModel.messages.size - 1)
                scrollToBottom()
            } catch (e: Exception) {
                if (_binding == null) return@launch
                removeLoadingMessage(loadingIndex)
                Toast.makeText(requireContext(), "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                if (_binding != null) {
                    setSending(false)
                } else {
                    viewModel.isSending = false
                }
            }
        }
    }

    private fun removeLoadingMessage(index: Int) {
        if (index in viewModel.messages.indices && viewModel.messages[index].isLoading) {
            viewModel.messages.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }

    private fun setSending(sending: Boolean) {
        viewModel.isSending = sending
        binding.btnSend.isEnabled = !sending
        binding.etMessage.isEnabled = !sending
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
