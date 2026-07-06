package com.nusiss.wellness.ui.main

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nusiss.wellness.R
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
        val context = holder.itemView.context
        val isSleep = item.type == "SLEEP"
        holder.binding.ivTypeIcon.setImageResource(if (isSleep) R.drawable.ic_sleep else R.drawable.ic_exercise)
        holder.binding.ivTypeIcon.setBackgroundResource(if (isSleep) R.drawable.bg_circle_sleep else R.drawable.bg_circle_exercise)
        holder.binding.ivTypeIcon.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, if (isSleep) R.color.green_mid else R.color.white)
        )

        val desc = if (item.type == "SLEEP") {
            "Sleep ${item.value}${item.unit}"
        } else {
            val exerciseLabel = item.exerciseType?.takeIf { it.isNotBlank() } ?: "Exercise"
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