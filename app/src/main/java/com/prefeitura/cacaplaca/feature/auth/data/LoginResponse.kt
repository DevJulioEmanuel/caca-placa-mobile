package com.prefeitura.cacaplaca.feature.auth.data

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("agentId") val agentId: String,
    @SerializedName("name") val name: String
)