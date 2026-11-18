package com.prefeitura.cacaplaca.feature.profile.data.repository

import com.prefeitura.cacaplaca.feature.profile.data.api.ProfileApi
import com.prefeitura.cacaplaca.feature.profile.data.mapper.toUiModel
import com.prefeitura.cacaplaca.feature.profile.data.model.ProfileUiModel
import com.prefeitura.cacaplaca.feature.profile.data.model.UpdateProfileRequest

class ProfileRepository(private val api: ProfileApi) {

    suspend fun getProfile(): Result<ProfileUiModel> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toUiModel())
            } else {
                Result.failure(Exception("Erro ao buscar dados do perfil."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(nome: String, email: String): Result<ProfileUiModel> {
        return try {
            val response = api.updateProfile(UpdateProfileRequest(nome = nome, email = email))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toUiModel())
            } else {
                Result.failure(Exception("Erro ao atualizar perfil."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
