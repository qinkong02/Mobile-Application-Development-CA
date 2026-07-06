package com.nusiss.wellness.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.nusiss.wellness.R
import com.nusiss.wellness.data.api.RetrofitClient
import com.nusiss.wellness.data.api.TokenManager
import com.nusiss.wellness.data.model.UserProfile
import com.nusiss.wellness.databinding.FragmentProfileBinding
import com.nusiss.wellness.ui.login.LoginActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvUserName.text = TokenManager.getUserName()

        // added by XieMaonan：读取/保存身高体重年龄性别，供 chatbot 工具调用做个性化建议
        loadProfile()
        binding.btnSaveProfile.setOnClickListener { saveProfile() }

        binding.btnLogout.setOnClickListener {
            TokenManager.clear()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getUserProfile()
                if (_binding == null) return@launch
                val profile = if (response.isSuccessful) response.body()?.data else null
                profile?.heightCm?.let { binding.etHeight.setText(it.toString()) }
                profile?.weightKg?.let { binding.etWeight.setText(it.toString()) }
                profile?.age?.let { binding.etAge.setText(it.toString()) }
                when (profile?.gender) {
                    "MALE" -> binding.rgGender.check(R.id.rbMale)
                    "FEMALE" -> binding.rgGender.check(R.id.rbFemale)
                }
            } catch (e: Exception) {
                // 拉取失败静默忽略，用户可以直接手动填写
            }
        }
    }

    private fun saveProfile() {
        val heightCm = binding.etHeight.text.toString().trim().toIntOrNull()
        val weightKg = binding.etWeight.text.toString().trim().toDoubleOrNull()
        val age = binding.etAge.text.toString().trim().toIntOrNull()
        val gender = when (binding.rgGender.checkedRadioButtonId) {
            R.id.rbMale -> "MALE"
            R.id.rbFemale -> "FEMALE"
            else -> null
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.updateUserProfile(
                    UserProfile(heightCm, weightKg, age, gender)
                )
                if (_binding == null) return@launch
                val messageRes = if (response.isSuccessful) {
                    R.string.profile_saved_toast
                } else {
                    R.string.profile_save_failed_toast
                }
                Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                if (_binding == null) return@launch
                Toast.makeText(requireContext(), R.string.profile_save_failed_toast, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
