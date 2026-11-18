package com.prefeitura.cacaplaca.feature.profile.data.api

import com.prefeitura.cacaplaca.feature.profile.data.model.UpdateProfileRequest
import com.prefeitura.cacaplaca.feature.profile.data.model.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface ProfileApi {
    @GET("users/me")
    suspend fun getProfile(): Response<UserProfileResponse>

    @PATCH("users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>
}
