package com.prefeitura.cacaplaca.feature.auth.data

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: Int,
    val nome: String,
    val email: String,
    val role: String,
    @SerializedName("is_active")
    val isActive: Boolean
)
