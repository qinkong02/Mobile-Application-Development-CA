/**
 * @author ZhangMingchang
 */
package com.nusiss.wellness.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.nusiss.wellness.R
import com.nusiss.wellness.data.api.RetrofitClient
import com.nusiss.wellness.data.api.TokenManager
import com.nusiss.wellness.databinding.FragmentHomeBinding
import com.nusiss.wellness.ui.entry.AddRecordActivity
import com.nusiss.wellness.ui.recommendation.RecommendationActivity
import kotlinx.coroutines.launch
import android.content.Context

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvGreeting.text = "Hi, ${TokenManager.getUserName()}"

        binding.cardQuickEntry.setOnClickListener {
            startActivity(Intent(requireContext(), AddRecordActivity::class.java))
        }
        binding.cardQuickChat.setOnClickListener {
            (activity as? MainActivity)?.selectTab(R.id.nav_chat)
        }
        binding.cardAiSuggestion.setOnClickListener {
            startActivity(Intent(requireContext(), RecommendationActivity::class.java))
        }

        loadLatestRecommendation()
    }

    private fun loadLatestRecommendation() {
        val cached = requireContext()
            .getSharedPreferences("ai_insights", Context.MODE_PRIVATE)
            .getString("latest_recommendation_text", null)

        binding.tvAiSuggestion.text = when {
            cached.isNullOrBlank() -> "Tap to view or generate this week's insights"
            else -> {
                val idx = cached.indexOf("【总体评价】")
                if (idx >= 0) cached.substring(idx + "【总体评价】".length).trim()
                else "Insights ready — tap to view"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
