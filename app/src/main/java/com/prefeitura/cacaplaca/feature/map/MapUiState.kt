package com.prefeitura.cacaplaca.feature.map

data class MapUiState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLocationAvailable: Boolean = false,
    val isOffline: Boolean = false,
    val isOfflineBannerVisible: Boolean = true
)