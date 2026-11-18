package com.prefeitura.cacaplaca.feature.profile.ui.state

import com.prefeitura.cacaplaca.feature.profile.data.model.ProfileUiModel

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: ProfileUiModel? = null,
    val errorMessage: String? = null,
    val isUpdating: Boolean = false,
    val updateError: String? = null,
    val updateSuccess: Boolean = false
)
