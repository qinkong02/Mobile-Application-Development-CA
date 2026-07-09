/**
 * @author WengYuhao
 */
package com.nusiss.wellness.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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

        binding.btnBack.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener { doRegister() }
    }

    private fun doRegister() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()

        var isValid = true

        if (username.isEmpty()) {
            binding.etUsername.error = "Username cannot be empty"
            isValid = false
        } else {
            binding.etUsername.error = null
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email cannot be empty"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email format"
            isValid = false
        } else {
            binding.etEmail.error = null
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password cannot be empty"
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.etPassword.error = null
        }

        if (confirm.isEmpty()) {
            binding.etConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirm) {
            binding.etConfirmPassword.error = "Passwords do not match"
            isValid = false
        } else {
            binding.etConfirmPassword.error = null
        }

        if (!isValid) return

        binding.btnRegister.isEnabled = false
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.register(RegisterRequest(username, email, password))
                val body = response.body()
                val data = body?.data
                if (response.isSuccessful && body?.success == true && data != null) {
                    TokenManager.saveToken(data.token)
                    TokenManager.saveUser(data.user.id.toString(), data.user.username)
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                } else {
                    // Handle server errors and display them on the specific field
                    val message = body?.message ?: "Username already exist"
                    
                    when {
                        message.contains("Username", ignoreCase = true) -> {
                            binding.etUsername.error = message
                            binding.etUsername.requestFocus()
                        }
                        message.contains("Email", ignoreCase = true) -> {
                            binding.etEmail.error = message
                            binding.etEmail.requestFocus()
                        }
                        else -> {
                            // If we can't determine the field, show a Toast
                            Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnRegister.isEnabled = true
            }
        }
    }
}
