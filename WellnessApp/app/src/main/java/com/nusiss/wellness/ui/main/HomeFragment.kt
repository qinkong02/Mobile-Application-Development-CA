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

        binding.tvGreeting.text = "你好，${TokenManager.getUserName()}"

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
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getLatestRecommendation()
                if (_binding == null) return@launch
                if (response.isSuccessful && response.body() != null) {
                    binding.tvAiSuggestion.text = response.body()!!.summary
                } else {
                    binding.tvAiSuggestion.text = "点击查看/生成本周健康建议"
                }
            } catch (e: Exception) {
                if (_binding == null) return@launch
                binding.tvAiSuggestion.text = "暂时无法获取建议，请检查网络"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
