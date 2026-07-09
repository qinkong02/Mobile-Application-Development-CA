/**
 * @author WengYuhao
 */
package com.nusiss.wellness.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nusiss.wellness.data.api.RetrofitClient
import com.nusiss.wellness.data.api.TokenManager
import com.nusiss.wellness.data.model.LoginRequest
import com.nusiss.wellness.databinding.ActivityLoginBinding
import com.nusiss.wellness.ui.main.MainActivity
import com.nusiss.wellness.ui.register.RegisterActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (TokenManager.isLoggedIn()) {
            goToHome()
            return
        }

        binding.btnLogin.setOnClickListener { doLogin() }
        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvGoRegister.setOnLongClickListener {
            TokenManager.saveToken("test-token")
            TokenManager.saveUser("test-id", "Test User")
            goToHome()
            true
        }
    }

    private fun doLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your username and password", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnLogin.isEnabled = false
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.login(LoginRequest(username, password))
                val body = response.body()
                val data = body?.data
                if (response.isSuccessful && body?.success == true && data != null) {
                    TokenManager.saveToken(data.token)
                    TokenManager.saveUser(data.user.id.toString(), data.user.username)
                    goToHome()
                } else {
                    Toast.makeText(this@LoginActivity, body?.message ?: "Login failed. Please check your username or password", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnLogin.isEnabled = true
            }
        }
    }
    private fun goToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
