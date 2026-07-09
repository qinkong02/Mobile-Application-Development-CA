/**
 * @author WengYuhao
 */
package com.nusiss.wellness.data.model

// added by XieMaonan：对应后端 GET/PUT /api/user/profile，供 chatbot 个性化建议使用
data class UserProfile(
    val heightCm: Int? = null,
    val weightKg: Double? = null,
    val age: Int? = null,
    val gender: String? = null
)
