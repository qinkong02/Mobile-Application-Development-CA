package com.nusiss.wellness.data.model

data class RecommendationItem(
    val id: String? = null,
    val title: String,
    val reason: String = "",
    val createdAt: String? = null
)

data class RecommendationResponse(
    val summary: String,
    val items: List<RecommendationItem>
)

data class RecommendationDTO(
    val id: Long? = null,
    val userId: Long? = null,
    val recommendationText: String = "",
    val avgSleepHours: Double? = null,
    val totalExerciseMinutes: Int? = null,
    val generatedAt: String? = null,
    val isRead: Boolean? = null
)