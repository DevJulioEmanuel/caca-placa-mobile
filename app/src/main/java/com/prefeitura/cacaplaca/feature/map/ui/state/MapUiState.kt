package com.prefeitura.cacaplaca.feature.map.ui.state

import com.prefeitura.cacaplaca.feature.map.data.model.PlacaUiModel

data class MapUiState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLocationAvailable: Boolean = false,
    val isOffline: Boolean = false,
    val isOfflineBannerVisible: Boolean = true,
    val placas: List<PlacaUiModel> = emptyList(),
    val selectedPlaca: PlacaUiModel? = null
)
