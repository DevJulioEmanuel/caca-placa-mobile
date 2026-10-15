package com.prefeitura.cacaplaca.feature.auth.data

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoginResponse>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun resetPassword(token: String, newPassword: String): Result<Unit>
}