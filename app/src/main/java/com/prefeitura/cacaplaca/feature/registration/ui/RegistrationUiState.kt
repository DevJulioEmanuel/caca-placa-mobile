package com.prefeitura.cacaplaca.feature.registration.ui

import com.prefeitura.cacaplaca.feature.registration.data.*

data class RegistrationUiState(
    val currentStep: RegistrationStep = RegistrationStep.CAMERA,
    val photo: CapturedPhoto? = null,
    val gpsState: GpsUiState = GpsUiState.Loading,
    val availablePlates: List<PlateTypeOption> = availablePlatesList,
    val selectedPlate: PlateTypeOption? = null,
    val isSelectingPlate: Boolean = false,
    val plateSearchQuery: String = "",
    val tempSelectedPlate: PlateTypeOption? = null,
    val agentName: String = "",
    val registrationDate: String = "",
    val isAdditionalInfoExpanded: Boolean = false,
    val afastamentoInput: String = "",
    val estadoConservacao: EstadoConservacao = EstadoConservacao.BOM,
    val tipoPelicula: TipoPelicula? = null,
    val tipoSubstrato: TipoSubstrato? = null,
    val tipoSuporte: TipoSuporte? = null,
    val estadoPelicula: EstadoConservacao? = null,
    val estadoSubstrato: EstadoConservacao? = null,
    val estadoSuporte: EstadoConservacao? = null,
    val observacaoInput: String = "",
    val saveBannerState: SaveBannerState = SaveBannerState.NONE,
    val confirmationStatus: ConfirmationSyncStatus = ConfirmationSyncStatus.PENDING
) {
    val canSubmitForm: Boolean
        get() = photo != null &&
                selectedPlate != null &&
                gpsState is GpsUiState.Success &&
                saveBannerState != SaveBannerState.SAVING
}