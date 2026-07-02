package com.nusiss.wellness.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nusiss.wellness.data.model.WellnessRecord
import com.nusiss.wellness.databinding.ItemRecordBinding

class RecordsAdapter(private var items: List<WellnessRecord>) :
    RecyclerView.Adapter<RecordsAdapter.RecordViewHolder>() {

    fun updateData(newItems: List<WellnessRecord>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class RecordViewHolder(val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val item = items[position]
        val typeLabel = if (item.type == "SLEEP") "睡" else "动"
        holder.binding.tvTypeIcon.text = typeLabel
        val desc = if (item.type == "SLEEP") "睡眠 ${item.value}${item.unit}" else "运动 ${item.value}${item.unit}"
        holder.binding.tvDesc.text = desc
        holder.binding.tvDate.text = item.recordDate
    }

    override fun getItemCount() = items.size
}
