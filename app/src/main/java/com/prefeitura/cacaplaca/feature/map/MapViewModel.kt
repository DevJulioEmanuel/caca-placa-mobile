package com.prefeitura.cacaplaca.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.core.gps.LocationRepository
import com.prefeitura.cacaplaca.core.network.NetworkTracker
import com.prefeitura.cacaplaca.feature.map.data.MapUiState
import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse
import com.prefeitura.cacaplaca.feature.registration.data.repository.RegistrationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val locationRepository: LocationRepository,
    private val networkTracker: NetworkTracker,
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeNetwork()
        loadPlacas()
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

    fun selectPlaca(placa: PlacaItemResponse?) {
        _uiState.update { it.copy(selectedPlaca = placa) }
    }

    fun clearSelectedPlaca() {
        _uiState.update { it.copy(selectedPlaca = null) }
    }

    fun loadPlacas() {
        viewModelScope.launch {
            registrationRepository.listPlacas().onSuccess { response ->
                _uiState.update { state ->
                    state.copy(placas = response.items)
                }
            }.onFailure {
                // Ignore or handle error
            }
        }
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
