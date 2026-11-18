package com.prefeitura.cacaplaca.feature.map.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.core.gps.LocationRepository
import com.prefeitura.cacaplaca.core.network.NetworkTracker
import com.prefeitura.cacaplaca.feature.map.data.mapper.toUiModel
import com.prefeitura.cacaplaca.feature.map.data.model.PlacaUiModel
import com.prefeitura.cacaplaca.feature.map.ui.state.MapUiState
import com.prefeitura.cacaplaca.feature.registration.data.model.EventoPlacaResponse
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

    fun selectPlaca(placa: PlacaUiModel?) {
        _uiState.update { it.copy(selectedPlaca = placa, selectedPlacaEvents = emptyList()) }
        if (placa != null) {
            loadEventosForPlaca(placa)
        }
    }

    private fun loadEventosForPlaca(placa: PlacaUiModel) {
        viewModelScope.launch {
            val cachedUltimo = placa.ultimoEvento?.let { uiModel ->
                EventoPlacaResponse(
                    id = uiModel.id,
                    createdBy = uiModel.createdBy,
                    tipoEvento = uiModel.tipoEvento,
                    estadoConservacao = uiModel.estadoConservacao,
                    estadoPelicula = uiModel.estadoPelicula,
                    estadoSubstrato = uiModel.estadoSubstrato,
                    estadoSuporte = uiModel.estadoSuporte,
                    fotoUrl = uiModel.fotoUrl,
                    observacao = uiModel.observacao,
                    timestamp = uiModel.timestamp
                )
            }
            placa.id?.let { id ->
                registrationRepository.listEventos(id, cachedUltimo).onSuccess { eventos ->
                    _uiState.update { state ->
                        state.copy(selectedPlacaEvents = eventos.map { it.toUiModel() })
                    }
                }
            }
        }
    }

    fun clearSelectedPlaca() {
        _uiState.update { it.copy(selectedPlaca = null, selectedPlacaEvents = emptyList()) }
    }

    fun loadPlacas() {
        viewModelScope.launch {
            registrationRepository.listPlacas().onSuccess { response ->
                _uiState.update { state ->
                    state.copy(placas = response.items.map { it.toUiModel() })
                }
            }.onFailure {
                // Ignore or handle error
            }
        }
    }

    fun deletePlaca(placaId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            registrationRepository.deletePlaca(placaId).onSuccess {
                clearSelectedPlaca()
                loadPlacas()
                onSuccess()
            }.onFailure {
                // Handle error
            }
        }
    }

    fun deleteEventoPlaca(placaId: String, eventoId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            registrationRepository.deleteEventoPlaca(placaId, eventoId).onSuccess {
                loadPlacas()
                val currentSelected = uiState.value.selectedPlaca
                if (currentSelected != null && currentSelected.id == placaId) {
                    val updatedResponse = registrationRepository.listPlacas()
                    updatedResponse.onSuccess { listResp ->
                        val updatedPlaca = listResp.items.firstOrNull { it.id == placaId }?.toUiModel()
                        if (updatedPlaca != null) {
                            selectPlaca(updatedPlaca)
                        } else {
                            clearSelectedPlaca()
                        }
                    }
                }
                onSuccess()
            }.onFailure {
                // Handle error
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
