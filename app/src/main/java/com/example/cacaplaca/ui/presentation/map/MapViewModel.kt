package com.example.cacaplaca.ui.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cacaplaca.data.map.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MapViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    fun getLocation() {
        if (uiState.value.isLocationAvailable) return

        viewModelScope.launch {
            val location = locationRepository.getLocationUpdates().first()

            _uiState.value = MapUiState(
                latitude = location.latitude,
                longitude = location.longitude,
                isLocationAvailable = true
            )
        }
    }
}