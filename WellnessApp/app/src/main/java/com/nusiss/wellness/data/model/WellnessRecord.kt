package com.nusiss.wellness.data.model

data class WellnessRecord(
    val id: String? = null,
    val type: String,        // "SLEEP" 或 "EXERCISE"
    val value: Double,        // 睡眠小时数 / 运动分钟数
    val unit: String,         // "小时" / "分钟"
    val recordDate: String,   // "yyyy-MM-dd"
    val note: String? = null,
    val exerciseType: String? = null   // 运动类型，如 "跑步"/"瑜伽"，仅 EXERCISE 条目使用
)
