package com.prefeitura.cacaplaca.feature.profile.data.repository

import com.prefeitura.cacaplaca.feature.profile.data.api.ProfileApi
import com.prefeitura.cacaplaca.feature.profile.data.local.dao.ProfileDao
import com.prefeitura.cacaplaca.feature.profile.data.mapper.toEntity
import com.prefeitura.cacaplaca.feature.profile.data.mapper.toUiModel
import com.prefeitura.cacaplaca.feature.profile.data.model.ProfileUiModel
import com.prefeitura.cacaplaca.feature.profile.data.model.UpdateProfileRequest

class ProfileRepository(
    private val api: ProfileApi,
    private val profileDao: ProfileDao
) {

    suspend fun getProfile(): Result<ProfileUiModel> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                profileDao.insertProfile(body.toEntity())
                Result.success(body.toUiModel())
            } else {
                fetchFromLocalOrError(Exception("Erro na API: ${response.code()}"))
            }
        } catch (e: Exception) {
            fetchFromLocalOrError(e)
        }
    }

    private suspend fun fetchFromLocalOrError(fallbackError: Exception): Result<ProfileUiModel> {
        return try {
            val localProfile = profileDao.getProfile()
            if (localProfile != null) {
                Result.success(localProfile.toUiModel())
            } else {
                Result.failure(fallbackError)
            }
        } catch (e: Exception) {
            Result.failure(fallbackError)
        }
    }

    suspend fun updateProfile(nome: String, email: String): Result<ProfileUiModel> {
        return try {
            val response = api.updateProfile(UpdateProfileRequest(nome = nome, email = email))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                profileDao.insertProfile(body.toEntity())
                Result.success(body.toUiModel())
            } else {
                Result.failure(Exception("Erro ao atualizar perfil."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
