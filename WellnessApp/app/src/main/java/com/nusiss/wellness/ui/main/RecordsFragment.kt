package com.nusiss.wellness.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nusiss.wellness.data.api.RetrofitClient
import com.nusiss.wellness.databinding.FragmentRecordsBinding
import com.nusiss.wellness.ui.entry.AddRecordActivity
import kotlinx.coroutines.launch
import com.nusiss.wellness.data.model.WellnessRecord

class RecordsFragment : Fragment() {

    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RecordsAdapter

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

        loadRecords()
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
                                add(WellnessRecord(log.id?.toString(), "SLEEP", it, "小时", log.logDate, log.notes))
                            }
                            log.exerciseMinutes?.let {
                                add(WellnessRecord(log.id?.toString(), "EXERCISE", it.toDouble(), "分钟", log.logDate, log.notes, log.exerciseType))
                            }
                        }
                    }
                    adapter.updateData(records)
                }else {
                    Toast.makeText(requireContext(), "加载记录失败", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (_binding == null) return@launch
                Toast.makeText(requireContext(), "网络连接失败：${e.message}", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "记录ID无效，无法删除", Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle("删除记录")
            .setMessage("确定要删除这条记录吗？")
            .setPositiveButton("删除") { _, _ -> deleteRecord(id) }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun deleteRecord(id: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.deleteRecord(id)
                if (_binding == null) return@launch
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "删除成功", Toast.LENGTH_SHORT).show()
                    loadRecords()
                } else {
                    Toast.makeText(requireContext(), "删除失败，请重试", Toast.LENGTH_SHORT).show()
                }
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
