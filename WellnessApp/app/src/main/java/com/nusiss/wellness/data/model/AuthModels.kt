package com.nusiss.wellness.data.model

data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val username: String, val email: String, val password: String)

data class UserInfo(
    val id: Long,
    val username: String,
    val email: String?,
    val role: String?
)

data class AuthResponse(
    val user: UserInfo,
    val token: String
)