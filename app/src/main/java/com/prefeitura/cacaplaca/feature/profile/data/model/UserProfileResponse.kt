package com.prefeitura.cacaplaca.feature.profile.data.model

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    val id: Int,
    val nome: String,
    val email: String,
    val role: String,
    @SerializedName("created_at")
    val createdAt: String?
)
