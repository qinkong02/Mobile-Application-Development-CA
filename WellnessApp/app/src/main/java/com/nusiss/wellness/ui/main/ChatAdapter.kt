package com.nusiss.wellness.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nusiss.wellness.R
import com.nusiss.wellness.data.model.ChatMessage
import com.nusiss.wellness.databinding.ItemChatBotBinding
import com.nusiss.wellness.databinding.ItemChatUserBinding

class ChatAdapter(private val items: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_USER = 0
        const val TYPE_BOT = 1
    }

    override fun getItemViewType(position: Int) =
        if (items[position].isUser) TYPE_USER else TYPE_BOT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_USER) {
            val binding = ItemChatUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            UserHolder(binding)
        } else {
            val binding = ItemChatBotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            BotHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is UserHolder -> holder.binding.tvMessage.text = item.text
            is BotHolder -> {
                val context = holder.binding.root.context
                if (item.isLoading) {
                    holder.binding.tvMessage.text = context.getString(R.string.chat_thinking)
                    holder.binding.tvMessage.setTextColor(
                        ContextCompat.getColor(context, R.color.text_secondary)
                    )
                } else {
                    holder.binding.tvMessage.text = item.text
                    holder.binding.tvMessage.setTextColor(
                        ContextCompat.getColor(context, R.color.green_primary)
                    )
                }
            }
        }
    }

    override fun getItemCount() = items.size

    class UserHolder(val binding: ItemChatUserBinding) : RecyclerView.ViewHolder(binding.root)
    class BotHolder(val binding: ItemChatBotBinding) : RecyclerView.ViewHolder(binding.root)
}
