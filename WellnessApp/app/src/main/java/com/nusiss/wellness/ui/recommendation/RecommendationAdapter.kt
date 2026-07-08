package com.nusiss.wellness.ui.recommendation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nusiss.wellness.data.model.RecommendationItem
import com.nusiss.wellness.databinding.ItemRecommendationBinding

class RecommendationAdapter(private var items: List<RecommendationItem>) :
    RecyclerView.Adapter<RecommendationAdapter.ItemHolder>() {

    fun updateData(newItems: List<RecommendationItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ItemHolder(val binding: ItemRecommendationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = items[position]
        holder.binding.tvTitle.text = item.title
        holder.binding.tvReason.text = item.reason
    }

    override fun getItemCount() = items.size
}
