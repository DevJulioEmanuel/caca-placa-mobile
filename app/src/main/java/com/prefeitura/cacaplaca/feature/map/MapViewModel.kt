package com.prefeitura.cacaplaca.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.core.gps.LocationRepository
import com.prefeitura.cacaplaca.core.network.NetworkTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val locationRepository: LocationRepository,
    private val networkTracker: NetworkTracker,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeNetwork()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkTracker.isOnlineFlow.collect { isOnline ->
                val isOffline = !isOnline
                _uiState.update { state ->
                    state.copy(
                        isOffline = isOffline,
                        isOfflineBannerVisible = isOffline || state.isOfflineBannerVisible,
                    )
                }
            }
        }
    }

    fun dismissOfflineBanner() {
        _uiState.update { it.copy(isOfflineBannerVisible = false) }
    }

    fun getLocation() {
        if (uiState.value.isLocationAvailable) return

        viewModelScope.launch {
            val location = locationRepository.getLocationUpdates().first()

            _uiState.update { state ->
                state.copy(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    isLocationAvailable = true,
                )
            }
        }
    }
}