/**
 * @author WengYuhao
 */
package com.nusiss.wellness.data.model

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)