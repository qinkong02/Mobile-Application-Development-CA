package com.nusiss.wellness.ui.recommendation

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nusiss.wellness.data.api.RetrofitClient
import com.nusiss.wellness.data.model.RecommendationItem
import com.nusiss.wellness.data.model.RecommendationResponse
import com.nusiss.wellness.databinding.ActivityRecommendationBinding
import kotlinx.coroutines.launch

class RecommendationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendationBinding
    private lateinit var adapter: RecommendationAdapter

    private val prefs by lazy { getSharedPreferences("ai_insights", Context.MODE_PRIVATE) }
    private val cacheKey = "latest_recommendation_text"
    private val emptyHint = "No insights yet. Tap the button below to generate"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RecommendationAdapter(emptyList())
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.adapter = adapter

        binding.btnBack.setOnClickListener { finish() }
        binding.btnRegenerate.setOnClickListener { generateRecommendation() }

        loadFromCache()
    }

    /** 打开时从本地缓存读上次结果；没有就显示空状态 */
    private fun loadFromCache() {
        val cached = prefs.getString(cacheKey, null)
        if (cached.isNullOrBlank()) {
            binding.tvSummary.text = emptyHint
            adapter.updateData(emptyList())
        } else {
            render(cached)
        }
    }

    private fun generateRecommendation() {
        binding.btnRegenerate.isEnabled = false
        binding.tvSummary.text = "Generating..."
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.generateRecommendation()
                val body = response.body()
                if (response.isSuccessful && body?.success == true && body.data != null) {
                    val text = body.data.recommendationText
                    prefs.edit().putString(cacheKey, text).apply()
                    render(text)
                } else {
                    // 后端在"近7天无记录"等情况会返回 400 + message，直接透出真实原因
                    val msg = body?.message
                        ?: response.errorBody()?.string()
                        ?: "Generation failed. Please try again"
                    binding.tvSummary.text = msg
                    Toast.makeText(this@RecommendationActivity, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RecommendationActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnRegenerate.isEnabled = true
            }
        }
    }

    private fun render(text: String) {
        val parsed = parse(text)
        binding.tvSummary.text = if (parsed.summary.isNotBlank()) parsed.summary else text
        adapter.updateData(parsed.items)
    }

    private fun parse(text: String): RecommendationResponse {
        val items = mutableListOf<RecommendationItem>()
        var summary = ""
        var summaryStarted = false
        val itemRegex = Regex("""^\s*\d+[.、)]\s*(.+)$""")
        for (raw in text.split("\n")) {
            val line = raw.trim()
            if (line.isEmpty()) continue
            when {
                line.contains("【总体评价】") -> {
                    summary = line.substringAfter("【总体评价】").trim()
                    summaryStarted = true
                }
                itemRegex.matches(line) ->
                    items.add(RecommendationItem(title = itemRegex.find(line)!!.groupValues[1].trim()))
                summaryStarted -> summary += " $line"  // 总评换行续写
            }
        }
        return RecommendationResponse(summary = summary, items = items)
    }
}