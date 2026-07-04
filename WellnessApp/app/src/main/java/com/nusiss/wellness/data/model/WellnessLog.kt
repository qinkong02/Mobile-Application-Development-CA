package com.nusiss.wellness.data.model

data class WellnessLog(
    val id: Long? = null,
    val logDate: String,
    val sleepHours: Double? = null,
    val exerciseType: String? = null,
    val exerciseMinutes: Int? = null,
    val moodScore: Int? = null,
    val notes: String? = null
)