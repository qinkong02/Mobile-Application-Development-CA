package com.nusiss.wellness.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nusiss.wellness.data.model.WellnessRecord
import com.nusiss.wellness.databinding.ItemRecordBinding

class RecordsAdapter(
    private var items: List<WellnessRecord>,
    private val onLongClick: (WellnessRecord) -> Unit
) : RecyclerView.Adapter<RecordsAdapter.RecordViewHolder>() {

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

        val desc = if (item.type == "SLEEP") {
            "睡眠 ${item.value}${item.unit}"
        } else {
            val exerciseLabel = item.exerciseType?.takeIf { it.isNotBlank() } ?: "运动"
            "$exerciseLabel ${item.value.toInt()}${item.unit}"
        }
        holder.binding.tvDesc.text = desc
        holder.binding.tvDate.text = item.recordDate

        if (!item.note.isNullOrBlank()) {
            holder.binding.tvNote.text = item.note
            holder.binding.tvNote.visibility = View.VISIBLE
        } else {
            holder.binding.tvNote.visibility = View.GONE
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(item)
            true
        }
    }

    override fun getItemCount() = items.size
}