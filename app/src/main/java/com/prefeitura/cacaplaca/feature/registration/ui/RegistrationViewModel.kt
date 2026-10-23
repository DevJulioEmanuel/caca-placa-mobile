package com.prefeitura.cacaplaca.feature.registration.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.core.session.SessionManager
import com.prefeitura.cacaplaca.core.gps.LocationTracker
import com.prefeitura.cacaplaca.feature.registration.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.File

class RegistrationViewModel(
    private val repository: RegistrationRepository,
    private val sessionManager: SessionManager,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private val allPlates = availablePlatesList

    init {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        _uiState.update {
            it.copy(
                agentName = sessionManager.getUserName() ?: "Agente",
                registrationDate = dateFormat.format(Date())
            )
        }
    }

    fun fetchGpsLocation(simulateError: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(gpsState = GpsUiState.Loading) }
            val location = locationTracker.getCurrentLocation()
            if (location != null && !simulateError) {
                _uiState.update { 
                    it.copy(gpsState = GpsUiState.Success(location.latitude, location.longitude)) 
                }
            } else {
                _uiState.update { it.copy(gpsState = GpsUiState.Error) }
            }
        }
    }

    fun onHandleBack(): Boolean {
        val currentState = _uiState.value
        return when (currentState.currentStep) {
            RegistrationStep.FORM -> {
                _uiState.update { it.copy(currentStep = RegistrationStep.CAMERA) }
                true
            }
            RegistrationStep.CONFIRMATION -> {
                resetRegistrationState()
                true
            }
            RegistrationStep.CAMERA -> false
        }
    }

    fun onClosePlateSelection() {
        _uiState.update { it.copy(isSelectingPlate = false) }
    }

    fun onSearchQueryChanged(query: String) {
        val filtered = if (query.isBlank()) {
            allPlates
        } else {
            allPlates.filter { it.displayLabel.contains(query, ignoreCase = true) || it.code.contains(query, ignoreCase = true) }
        }
        _uiState.update { it.copy(plateSearchQuery = query, availablePlates = filtered) }
    }

    fun onTempPlateSelected(plate: PlateTypeOption) {
        _uiState.update { it.copy(tempSelectedPlate = plate) }
    }

    fun onConfirmPlateSelection() {
        _uiState.update { 
            it.copy(
                selectedPlate = it.tempSelectedPlate,
                isSelectingPlate = false
            )
        }
    }

    fun onPhotoCaptured(path: String) {
        _uiState.update { it.copy(photo = CapturedPhoto(localPath = path, name = File(path).name)) }
    }

    fun onContinueFromCamera() {
        _uiState.update { it.copy(currentStep = RegistrationStep.FORM) }
        if (_uiState.value.gpsState !is GpsUiState.Success) {
            fetchGpsLocation()
        }
    }

    fun onRemovePhoto() {
        _uiState.update { it.copy(photo = null, currentStep = RegistrationStep.CAMERA) }
    }

    fun onOpenPlateSelection() {
        _uiState.update { 
            it.copy(
                isSelectingPlate = true, 
                tempSelectedPlate = it.selectedPlate,
                plateSearchQuery = "",
                availablePlates = allPlates
            ) 
        }
    }

    fun onToggleAdditionalInfo() {
        _uiState.update { it.copy(isAdditionalInfoExpanded = !it.isAdditionalInfoExpanded) }
    }

    fun onAfastamentoChanged(value: String) {
        _uiState.update { it.copy(afastamentoInput = value) }
    }

    fun onEstadoConservacaoSelected(value: EstadoConservacao) {
        _uiState.update { it.copy(estadoConservacao = value) }
    }

    fun onTipoPeliculaSelected(value: TipoPelicula?) {
        _uiState.update { it.copy(tipoPelicula = value) }
    }

    fun onTipoSubstratoSelected(value: TipoSubstrato?) {
        _uiState.update { it.copy(tipoSubstrato = value) }
    }

    fun onTipoSuporteSelected(value: TipoSuporte?) {
        _uiState.update { it.copy(tipoSuporte = value) }
    }

    fun onObservacaoChanged(value: String) {
        _uiState.update { it.copy(observacaoInput = value) }
    }

    fun onRegisterAnotherPlate() {
        resetRegistrationState()
    }

    private fun resetRegistrationState() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        _uiState.update {
            RegistrationUiState(
                agentName = sessionManager.getUserName() ?: "Agente",
                registrationDate = dateFormat.format(Date()),
                availablePlates = allPlates
            )
        }
    }

    fun onSubmitRegistration() {
        val currentState = _uiState.value
        val gps = currentState.gpsState as? GpsUiState.Success ?: return
        val plate = currentState.selectedPlate ?: return
        val photo = currentState.photo ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(saveBannerState = SaveBannerState.SAVING) }

            val formData = PlacaCreateData(
                codigoContran = plate.code,
                latitude = gps.latitude,
                longitude = gps.longitude,
                estadoConservacao = currentState.estadoConservacao,
                afastamento = currentState.afastamentoInput.replace(",", ".").toFloatOrNull(),
                tipoPelicula = currentState.tipoPelicula,
                tipoSubstrato = currentState.tipoSubstrato,
                tipoSuporte = currentState.tipoSuporte,
                estadoPelicula = currentState.estadoPelicula,
                estadoSubstrato = currentState.estadoSubstrato,
                estadoSuporte = currentState.estadoSuporte,
                observacao = currentState.observacaoInput.ifBlank { null }
            )

            val result = repository.submitRegistration(formData, photo)

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        saveBannerState = SaveBannerState.NONE,
                        currentStep = RegistrationStep.CONFIRMATION,
                        confirmationStatus = ConfirmationSyncStatus.SYNCED
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(saveBannerState = SaveBannerState.ERROR) }
            }
        }
    }
}
