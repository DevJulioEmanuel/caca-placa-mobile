package com.prefeitura.cacaplaca.feature.registration.ui

sealed class GpsUiState {
    object Loading : GpsUiState()
    data class Success(val latitude: Double, val longitude: Double) : GpsUiState()
    object Error : GpsUiState()
}

enum class RegistrationStep {
    CAMERA, FORM, CONFIRMATION
}

enum class SaveBannerState {
    NONE, SAVING, ERROR, OFFLINE_WARNING
}

enum class ConfirmationSyncStatus {
    PENDING, SYNCED, FAILED
}