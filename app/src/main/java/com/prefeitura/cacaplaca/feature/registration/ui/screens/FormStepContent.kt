package com.prefeitura.cacaplaca.feature.registration.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prefeitura.cacaplaca.feature.registration.data.model.EstadoConservacao
import com.prefeitura.cacaplaca.feature.registration.data.model.TipoPelicula
import com.prefeitura.cacaplaca.feature.registration.data.model.TipoSubstrato
import com.prefeitura.cacaplaca.feature.registration.data.model.TipoSuporte
import com.prefeitura.cacaplaca.feature.registration.ui.components.*
import com.prefeitura.cacaplaca.feature.registration.ui.state.*
import com.prefeitura.cacaplaca.ui.theme.*

@Composable
fun FormStepContent(
    uiState: RegistrationUiState,
    onBackClick: () -> Unit,
    onRemovePhotoClick: () -> Unit,
    onOpenPlateSelectionClick: () -> Unit,
    onRetryGpsClick: () -> Unit,
    onToggleAdditionalInfo: () -> Unit,
    onAfastamentoChanged: (String) -> Unit,
    onEstadoConservacaoSelected: (EstadoConservacao) -> Unit,
    onTipoPeliculaSelected: (TipoPelicula?) -> Unit,
    onTipoSubstratoSelected: (TipoSubstrato?) -> Unit,
    onTipoSuporteSelected: (TipoSuporte?) -> Unit,
    onObservacaoChanged: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CacaPlacaBackground)
    ) {
        RegistrationTopBar(
            title = "Dados da placa",
            subtitle = "Etapa 2 de 2",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            if (uiState.photo != null) {
                RegistrationPhotoSection(
                    photo = uiState.photo,
                    onRemovePhotoClick = onRemovePhotoClick
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceWhite,
                border = BorderStroke(1.dp, OutlineGray.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Dados da placa",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Preencha o tipo e confira os dados automáticos.\n* Campo obrigatório",
                        style = MaterialTheme.typography.bodySmall,
                        color = CacaPlacaTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Código/tipo da placa *",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenPlateSelectionClick() },
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceWhite,
                        border = BorderStroke(1.dp, OutlineGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = uiState.selectedPlate?.displayLabel ?: "Selecione a placa",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CacaPlacaTextPrimary
                            )
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Pesquisar",
                                tint = CacaPlacaTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Pesquise e selecione o código da placa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CacaPlacaTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    RegistrationGpsCard(
                        gpsState = uiState.gpsState,
                        registrationDate = uiState.registrationDate,
                        agentName = uiState.agentName,
                        onRetryGpsClick = onRetryGpsClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    RegistrationAdditionalInfo(
                        expanded = uiState.isAdditionalInfoExpanded,
                        afastamentoInput = uiState.afastamentoInput,
                        estadoConservacao = uiState.estadoConservacao,
                        tipoPelicula = uiState.tipoPelicula,
                        tipoSubstrato = uiState.tipoSubstrato,
                        tipoSuporte = uiState.tipoSuporte,
                        observacaoInput = uiState.observacaoInput,
                        onToggleExpanded = onToggleAdditionalInfo,
                        onAfastamentoChanged = onAfastamentoChanged,
                        onEstadoConservacaoSelected = onEstadoConservacaoSelected,
                        onTipoPeliculaSelected = onTipoPeliculaSelected,
                        onTipoSubstratoSelected = onTipoSubstratoSelected,
                        onTipoSuporteSelected = onTipoSuporteSelected,
                        onObservacaoChanged = onObservacaoChanged
                    )
                }
            }

            if (uiState.saveBannerState != SaveBannerState.NONE) {
                Spacer(modifier = Modifier.height(16.dp))
                RegistrationStatusBanner(bannerState = uiState.saveBannerState)
            }

            Spacer(modifier = Modifier.height(16.dp))

            val isErrorState = uiState.saveBannerState == SaveBannerState.ERROR
            if (isErrorState) {
                OutlinedButton(
                    onClick = onSubmitClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SurfaceWhite,
                        contentColor = CacaPlacaTextPrimary
                    ),
                    border = BorderStroke(1.dp, CacaPlacaOrange)
                ) {
                    Text(
                        text = "Tentar novamente",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                Button(
                    onClick = onSubmitClick,
                    enabled = uiState.canSubmitForm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CacaPlacaOrange,
                        contentColor = CacaPlacaTextPrimary,
                        disabledContainerColor = SurfaceWhite,
                        disabledContentColor = CacaPlacaTextSecondary
                    ),
                    border = if (!uiState.canSubmitForm) BorderStroke(1.dp, OutlineGray) else null
                ) {
                    if (uiState.canSubmitForm) {
                        Icon(
                            imageVector = Icons.Outlined.AddCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "Cadastrar placa",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
