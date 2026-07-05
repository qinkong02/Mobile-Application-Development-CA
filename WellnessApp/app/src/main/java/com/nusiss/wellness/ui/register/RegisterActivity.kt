package com.nusiss.wellness.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nusiss.wellness.data.api.RetrofitClient
import com.nusiss.wellness.data.api.TokenManager
import com.nusiss.wellness.data.model.RegisterRequest
import com.nusiss.wellness.databinding.ActivityRegisterBinding
import com.nusiss.wellness.ui.main.MainActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { doRegister() }
    }

    private fun doRegister() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请完整填写信息", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirm) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnRegister.isEnabled = false
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.register(RegisterRequest(username, email, password))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    TokenManager.saveToken(body.token)
                    TokenManager.saveUser(body.userId, body.userName)
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "网络连接失败：${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnRegister.isEnabled = true
            }
        }
    }
}
