package com.nusiss.wellness.ui.recommendation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nusiss.wellness.data.api.RetrofitClient
import com.nusiss.wellness.databinding.ActivityRecommendationBinding
import kotlinx.coroutines.launch

class RecommendationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendationBinding
    private lateinit var adapter: RecommendationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RecommendationAdapter(emptyList())
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.adapter = adapter

        binding.btnBack.setOnClickListener { finish() }
        binding.btnRegenerate.setOnClickListener { generateRecommendation() }

        loadLatest()
    }

    private fun loadLatest() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getLatestRecommendation()
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    binding.tvSummary.text = body.summary
                    adapter.updateData(body.items)
                } else {
                    binding.tvSummary.text = "暂无建议，点击下方按钮生成"
                }
            } catch (e: Exception) {
                binding.tvSummary.text = "网络连接失败：${e.message}"
            }
        }
    }

    private fun generateRecommendation() {
        binding.btnRegenerate.isEnabled = false
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.generateRecommendation()
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    binding.tvSummary.text = body.summary
                    adapter.updateData(body.items)
                } else {
                    Toast.makeText(this@RecommendationActivity, "生成失败，请重试", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RecommendationActivity, "网络连接失败：${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnRegenerate.isEnabled = true
            }
        }
    }
}
