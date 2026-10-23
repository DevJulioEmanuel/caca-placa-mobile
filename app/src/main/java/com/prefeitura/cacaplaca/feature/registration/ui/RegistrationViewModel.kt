package com.prefeitura.cacaplaca.feature.registration.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.feature.registration.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class RegistrationStep {
    CAMERA,       // Etapa 1 de 2
    FORM,         // Etapa 2 de 2
    CONFIRMATION  // Confirmação
}

sealed interface GpsUiState {
    data object Loading : GpsUiState
    data class Success(
        val latitude: Double,
        val longitude: Double,
        val accuracyMeters: Int = 8,
        val isSimulated: Boolean = true
    ) : GpsUiState
    data object Error : GpsUiState
}

enum class SaveBannerState {
    NONE,
    OFFLINE_WARNING,
    SAVING,
    ERROR
}

enum class ConfirmationSyncStatus {
    SYNCED,
    SAVED_OFFLINE
}

data class RegistrationUiState(
    val currentStep: RegistrationStep = RegistrationStep.CAMERA,
    val photo: CapturedPhoto? = null,
    val selectedPlate: PlateTypeOption? = PlateTypeOption("R-1", "Parada obrigatória"),
    val availablePlates: List<PlateTypeOption> = listOf(
        PlateTypeOption("R-1", "Parada obrigatória"),
        PlateTypeOption("R-2", "Dê a preferência"),
        PlateTypeOption("R-19", "Velocidade máxima permitida"),
        PlateTypeOption("A-18", "Saliência ou lombada")
    ),
    val gpsState: GpsUiState = GpsUiState.Success(
        latitude = -4.9714,
        longitude = -39.0154,
        accuracyMeters = 8,
        isSimulated = true
    ),
    val registrationDate: String = "15/10/2026",
    val agentName: String = "Agente Pedro",
    val isAdditionalInfoExpanded: Boolean = false,
    val afastamentoInput: String = "",
    val estadoConservacao: EstadoConservacao = EstadoConservacao.BOM,
    val tipoPelicula: TipoPelicula? = null,
    val tipoSubstrato: TipoSubstrato? = null,
    val tipoSuporte: TipoSuporte? = null,
    val estadoPelicula: EstadoConservacao? = null,
    val estadoSubstrato: EstadoConservacao? = null,
    val estadoSuporte: EstadoConservacao? = null,
    val saveBannerState: SaveBannerState = SaveBannerState.NONE,
    val confirmationStatus: ConfirmationSyncStatus = ConfirmationSyncStatus.SYNCED,
    val isSelectingPlate: Boolean = false,
    val plateSearchQuery: String = "",
    val tempSelectedPlate: PlateTypeOption? = null,
) {
    val canSubmitForm: Boolean
        get() = photo != null &&
                selectedPlate != null &&
                gpsState is GpsUiState.Success &&
                saveBannerState != SaveBannerState.SAVING
}

class RegistrationViewModel(
    private val repository: RegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun onPhotoCaptured(filePath: String) {
        if (filePath.isBlank()) {
            _uiState.update { it.copy(photo = null) }
        } else {
            val captured = CapturedPhoto(
                id = UUID.randomUUID().toString(),
                localPath = filePath
            )
            _uiState.update { it.copy(photo = captured) }
        }
    }

    fun onRemovePhoto() {
        _uiState.update {
            it.copy(
                photo = null,
                currentStep = RegistrationStep.CAMERA
            )
        }
    }

    fun onContinueFromCamera() {
        if (_uiState.value.photo == null) return
        _uiState.update { it.copy(currentStep = RegistrationStep.FORM) }
        fetchGpsLocation()
    }

    fun fetchGpsLocation(simulateError: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(gpsState = GpsUiState.Loading) }
            delay(1200)
            if (simulateError) {
                _uiState.update { it.copy(gpsState = GpsUiState.Error) }
            } else {
                _uiState.update {
                    it.copy(
                        gpsState = GpsUiState.Success(
                            latitude = -4.9714,
                            longitude = -39.0154,
                            accuracyMeters = 8,
                            isSimulated = true
                        )
                    )
                }
            }
        }
    }

    fun onPlateSelected(plate: PlateTypeOption) {
        _uiState.update { it.copy(selectedPlate = plate) }
    }

    fun onToggleAdditionalInfo() {
        _uiState.update { it.copy(isAdditionalInfoExpanded = !it.isAdditionalInfoExpanded) }
    }

    fun onAfastamentoChanged(value: String) {
        _uiState.update { it.copy(afastamentoInput = value) }
    }

    fun onEstadoConservacaoSelected(estado: EstadoConservacao) {
        _uiState.update { it.copy(estadoConservacao = estado) }
    }

    fun onTipoPeliculaSelected(tipo: TipoPelicula?) {
        _uiState.update { it.copy(tipoPelicula = tipo) }
    }

    fun onTipoSubstratoSelected(tipo: TipoSubstrato?) {
        _uiState.update { it.copy(tipoSubstrato = tipo) }
    }

    fun onTipoSuporteSelected(tipo: TipoSuporte?) {
        _uiState.update { it.copy(tipoSuporte = tipo) }
    }

    fun onEstadoPeliculaSelected(estado: EstadoConservacao?) {
        _uiState.update { it.copy(estadoPelicula = estado) }
    }

    fun onEstadoSubstratoSelected(estado: EstadoConservacao?) {
        _uiState.update { it.copy(estadoSubstrato = estado) }
    }

    fun onEstadoSuporteSelected(estado: EstadoConservacao?) {
        _uiState.update { it.copy(estadoSuporte = estado) }
    }

    fun onOpenPlateSelection() {
        _uiState.update {
            it.copy(
                isSelectingPlate = true,
                tempSelectedPlate = it.selectedPlate,
                plateSearchQuery = ""
            )
        }
    }

    fun onClosePlateSelection() {
        _uiState.update { it.copy(isSelectingPlate = false) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(plateSearchQuery = query) }
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
                estadoSuporte = currentState.estadoSuporte
            )

            val result = repository.submitRegistration(
                data = formData,
                photo = photo
            )

            result.onSuccess { isSyncedOnline ->
                _uiState.update {
                    it.copy(
                        saveBannerState = SaveBannerState.NONE,
                        currentStep = RegistrationStep.CONFIRMATION,
                        confirmationStatus = ConfirmationSyncStatus.SYNCED
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(saveBannerState = SaveBannerState.ERROR) }
            }
        }
    }

    fun onRegisterAnotherPlate() {
        _uiState.value = RegistrationUiState()
    }

    fun onHandleBack(): Boolean {
        return when (_uiState.value.currentStep) {
            RegistrationStep.CAMERA -> false
            RegistrationStep.FORM -> {
                _uiState.update { it.copy(currentStep = RegistrationStep.CAMERA) }
                true
            }
            RegistrationStep.CONFIRMATION -> false
        }
    }
}