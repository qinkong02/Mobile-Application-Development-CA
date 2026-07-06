package com.nusiss.wellness.data.api

import com.nusiss.wellness.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---------- 认证 ----------
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    // ---------- 健康记录(对接后端 /api/wellness)----------
    @GET("api/wellness")
    suspend fun getRecords(): Response<ApiResponse<List<WellnessLog>>>

    @POST("api/wellness")
    suspend fun addRecord(@Body log: WellnessLog): Response<ApiResponse<WellnessLog>>

    @DELETE("api/wellness/{id}")
    suspend fun deleteRecord(@Path("id") id: Long): Response<ApiResponse<String>>

    // ---------- 聊天机器人 ----------
    @POST("api/chat")
    suspend fun sendChatMessage(@Body request: ChatRequest): Response<ChatResponse>

    // added by XieMaonan：原来没有拉取历史记录的接口，聊天记录只存在前端本地，
    // Fragment 一销毁就丢失。新增这个接口配合 ChatViewModel 在页面重建/App 重启时
    // 从后端恢复历史记录。
    @GET("api/chat/history")
    suspend fun getChatHistory(): Response<ApiResponse<List<ChatHistoryItem>>>

    // ---------- Agentic AI 健康建议 ----------
    @POST("api/agent/generate")
    suspend fun generateRecommendation(): Response<ApiResponse<RecommendationDTO>>
}