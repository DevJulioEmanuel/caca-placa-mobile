package com.prefeitura.cacaplaca.feature.registration.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegistrationScreen(
    onBackToHome: () -> Unit,
    onNavigateToMap: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val handleBack = {
        if (uiState.isSelectingPlate) {
            viewModel.onClosePlateSelection()
        } else if (!viewModel.onHandleBack()) {
            onBackToHome()
        }
    }

    BackHandler {
        handleBack()
    }

    // Se a flag de selecionar placa estiver ativa, sobrepõe o novo ecrã
    if (uiState.isSelectingPlate) {
        PlateSelectionContent(
            modifier = modifier,
            query = uiState.plateSearchQuery,
            onQueryChanged = viewModel::onSearchQueryChanged,
            availablePlates = uiState.availablePlates,
            tempSelectedPlate = uiState.tempSelectedPlate,
            onTempPlateSelected = viewModel::onTempPlateSelected,
            onConfirmSelection = viewModel::onConfirmPlateSelection,
            onBackClick = { viewModel.onClosePlateSelection() }
        )
        return // Interrompe a composição do resto para focar neste ecrã
    }

    Crossfade(targetState = uiState.currentStep, label = "RegistrationStepAnim") { step ->
        when (step) {
            RegistrationStep.CAMERA -> {
                CameraStepContent(
                    modifier = modifier,
                    capturedPhotoPath = uiState.photo?.localPath,
                    onPhotoCaptured = viewModel::onPhotoCaptured,
                    onContinueClick = viewModel::onContinueFromCamera,
                    onBackClick = handleBack
                )
            }
            RegistrationStep.FORM -> {
                FormStepContent(
                    modifier = modifier,
                    uiState = uiState,
                    onBackClick = handleBack,
                    onRemovePhotoClick = viewModel::onRemovePhoto,
                    // onPlateSelected removido daqui pois agora é feito no PlateSelectionContent
                    onOpenPlateSelectionClick = viewModel::onOpenPlateSelection,
                    onRetryGpsClick = { viewModel.fetchGpsLocation(simulateError = false) },
                    onToggleAdditionalInfo = viewModel::onToggleAdditionalInfo,
                    onAfastamentoChanged = viewModel::onAfastamentoChanged,
                    onEstadoConservacaoSelected = viewModel::onEstadoConservacaoSelected,
                    onTipoPeliculaSelected = viewModel::onTipoPeliculaSelected,
                    onTipoSubstratoSelected = viewModel::onTipoSubstratoSelected,
                    onTipoSuporteSelected = viewModel::onTipoSuporteSelected,
                    onSubmitClick = { viewModel.onSubmitRegistration() }
                )
            }
            RegistrationStep.CONFIRMATION -> {
                ConfirmationStepContent(
                    modifier = modifier,
                    status = uiState.confirmationStatus,
                    plateLabel = uiState.selectedPlate?.displayLabel ?: "",
                    photoPath = uiState.photo?.localPath,
                    agentName = uiState.agentName,
                    date = uiState.registrationDate,
                    onViewOnMapClick = onNavigateToMap,
                    onRegisterAnotherClick = viewModel::onRegisterAnotherPlate,
                    onBackClick = onBackToHome
                )
            }
        }
    }
}