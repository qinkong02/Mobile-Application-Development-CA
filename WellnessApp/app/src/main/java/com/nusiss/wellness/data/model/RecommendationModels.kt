package com.nusiss.wellness.data.model

data class RecommendationItem(
    val id: String? = null,
    val title: String,
    val reason: String,
    val createdAt: String? = null
)

data class RecommendationResponse(
    val summary: String,
    val items: List<RecommendationItem>
)
