package com.nusiss.wellness.data.api

import com.nusiss.wellness.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---------- 认证 ----------
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // ---------- 健康记录 ----------
    @GET("api/records")
    suspend fun getRecords(): Response<List<WellnessRecord>>

    @POST("api/records")
    suspend fun addRecord(@Body record: WellnessRecord): Response<WellnessRecord>

    @DELETE("api/records/{id}")
    suspend fun deleteRecord(@Path("id") id: String): Response<Unit>

    // ---------- 聊天机器人 ----------
    @POST("api/chat")
    suspend fun sendChatMessage(@Body request: ChatRequest): Response<ChatResponse>

    // ---------- Agentic AI 健康建议 ----------
    @POST("api/recommendations/generate")
    suspend fun generateRecommendation(): Response<RecommendationResponse>

    @GET("api/recommendations/latest")
    suspend fun getLatestRecommendation(): Response<RecommendationResponse>
}
