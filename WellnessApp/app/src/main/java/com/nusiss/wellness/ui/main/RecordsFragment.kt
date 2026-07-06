package com.nusiss.wellness.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nusiss.wellness.R
import com.nusiss.wellness.data.api.RetrofitClient
import com.nusiss.wellness.databinding.FragmentRecordsBinding
import com.nusiss.wellness.ui.entry.AddRecordActivity
import kotlinx.coroutines.launch
import com.nusiss.wellness.data.model.WellnessRecord

class RecordsFragment : Fragment() {

    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RecordsAdapter

    private var allRecords: List<WellnessRecord> = emptyList()
    private var selectedFilter: String? = null // null = ALL, otherwise "SLEEP" / "EXERCISE"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RecordsAdapter(emptyList()) { record -> confirmDelete(record) }
        binding.rvRecords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecords.adapter = adapter

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddRecordActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener { loadRecords() }

        binding.chipFilterAll.setOnClickListener { selectFilter(null) }
        binding.chipFilterSleep.setOnClickListener { selectFilter("SLEEP") }
        binding.chipFilterExercise.setOnClickListener { selectFilter("EXERCISE") }

        loadRecords()
    }

    private fun selectFilter(filter: String?) {
        selectedFilter = filter
        updateFilterChipStyles()
        applyFilter()
    }

    private fun updateFilterChipStyles() {
        setChipSelected(binding.chipFilterAll, selectedFilter == null, R.drawable.bg_pill_accent, R.color.green_primary)
        setChipSelected(binding.chipFilterSleep, selectedFilter == "SLEEP", R.drawable.bg_pill_sleep_accent, R.color.green_primary)
        setChipSelected(binding.chipFilterExercise, selectedFilter == "EXERCISE", R.drawable.bg_pill_exercise_accent, R.color.white)
    }

    private fun setChipSelected(chip: TextView, selected: Boolean, selectedBg: Int, selectedTextColor: Int) {
        chip.setBackgroundResource(if (selected) selectedBg else R.drawable.bg_pill_outline)
        chip.setTextColor(
            ContextCompat.getColor(requireContext(), if (selected) selectedTextColor else R.color.text_secondary)
        )
    }

    private fun applyFilter() {
        val filtered = selectedFilter?.let { filter -> allRecords.filter { it.type == filter } } ?: allRecords
        adapter.updateData(filtered)
    }

    override fun onResume() {
        super.onResume()
        loadRecords()
    }

    private fun loadRecords() {
        if (_binding == null) return
        binding.swipeRefresh.isRefreshing = true
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getRecords()
                if (_binding == null) return@launch
                if (response.isSuccessful && response.body() != null) {
                    val logs = response.body()!!.data.orEmpty()
                    val records = logs.flatMap { log ->
                        buildList {
                            log.sleepHours?.let {
                                add(WellnessRecord(log.id?.toString(), "SLEEP", it, "h", log.logDate, log.notes))
                            }
                            log.exerciseMinutes?.let {
                                add(WellnessRecord(log.id?.toString(), "EXERCISE", it.toDouble(), "min", log.logDate, log.notes, log.exerciseType))
                            }
                        }
                    }
                    allRecords = records
                    applyFilter()
                }else {
                    Toast.makeText(requireContext(), "Failed to load records", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (_binding == null) return@launch
                Toast.makeText(requireContext(), "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                if (_binding != null) {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun confirmDelete(record: WellnessRecord) {
        val id = record.id?.toLongOrNull()
        if (id == null) {
            Toast.makeText(requireContext(), "Invalid record ID, cannot delete", Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Delete") { _, _ -> deleteRecord(id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteRecord(id: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.deleteRecord(id)
                if (_binding == null) return@launch
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_SHORT).show()
                    loadRecords()
                } else {
                    Toast.makeText(requireContext(), "Delete failed. Please try again", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (_binding == null) return@launch
                Toast.makeText(requireContext(), "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
