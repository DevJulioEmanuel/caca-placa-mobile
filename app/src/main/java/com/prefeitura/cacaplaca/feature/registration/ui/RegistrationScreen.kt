package com.prefeitura.cacaplaca.feature.registration.ui

import androidx.activity.compose.BackHandler
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
        if (!viewModel.onHandleBack()) {
            onBackToHome()
        }
    }

    BackHandler {
        handleBack()
    }

    when (uiState.currentStep) {
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
                onPlateSelected = viewModel::onPlateSelected,
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