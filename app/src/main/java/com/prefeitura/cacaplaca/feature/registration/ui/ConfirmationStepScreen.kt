package com.prefeitura.cacaplaca.feature.registration.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prefeitura.cacaplaca.feature.registration.ui.components.RegistrationTopBar

@Composable
fun ConfirmationStepContent(
    status: ConfirmationSyncStatus,
    plateLabel: String,
    agentName: String,
    date: String,
    onViewOnMapClick: () -> Unit,
    onRegisterAnotherClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSynced = status == ConfirmationSyncStatus.SYNCED

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RegistrationTopBar(
            title = "Confirmação",
            subtitle = "Cadastro concluído",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Card Principal
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Ícone circular bege no topo
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isSynced) "Placa cadastrada com sucesso!" else "Placa salva no dispositivo",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isSynced) {
                            "O cadastro foi enviado ao servidor e já está disponível no mapa."
                        } else {
                            "Você está sem conexão. O cadastro e a foto foram salvos neste aparelho."
                        },
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Banner Azul Escuro de Status de Sincronização
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CloudOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isSynced) "Sincronizado" else "Aguardando sincronização",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isSynced) {
                                    "O cadastro foi confirmado pelo servidor."
                                } else {
                                    "O cadastro será sincronizado automaticamente quando houver conexão."
                                },
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    if (isSynced) {
                        Spacer(modifier = Modifier.height(16.dp))
                        // Resumo interno com miniatura da foto (Layout Sincronizado)
                        PlateSummaryBox(
                            plateLabel = plateLabel,
                            agentName = agentName,
                            date = date,
                            showPhotoPreview = true
                        )
                    } else {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Ainda não enviado ao servidor. Você pode continuar trabalhando.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // No modo Offline, o card de resumo fica fora do card principal
            if (!isSynced) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    PlateSummaryBox(
                        plateLabel = plateLabel,
                        agentName = agentName,
                        date = date,
                        showPhotoPreview = false,
                        hasBorder = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botão Primário: Ver no mapa
            Button(
                onClick = onViewOnMapClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Ver no mapa",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Secundário: Cadastrar outra placa
            OutlinedButton(
                onClick = onRegisterAnotherClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Cadastrar outra placa",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun PlateSummaryBox(
    plateLabel: String,
    agentName: String,
    date: String,
    showPhotoPreview: Boolean,
    hasBorder: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (hasBorder) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
        border = if (hasBorder) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = plateLabel,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (showPhotoPreview) {
                Spacer(modifier = Modifier.height(12.dp))
                // Placeholder para a foto única (quando ligar ao MinIO/câmara real, usa AsyncImage do Coil aqui)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pré-visualização da foto",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "1 foto • Localização obtida",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = agentName,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = date,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, name = "Confirmação Sincronizada")
@Composable
private fun ConfirmationSyncedPreview() {
    ConfirmationStepContent(
        status = ConfirmationSyncStatus.SYNCED,
        plateLabel = "R-1 — Parada obrigatória",
        agentName = "Agente Pedro",
        date = "15/10/2026",
        onViewOnMapClick = {},
        onRegisterAnotherClick = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "Confirmação Offline")
@Composable
private fun ConfirmationOfflinePreview() {
    ConfirmationStepContent(
        status = ConfirmationSyncStatus.SAVED_OFFLINE,
        plateLabel = "R-1 — Parada obrigatória",
        agentName = "Agente Pedro",
        date = "15/10/2026",
        onViewOnMapClick = {},
        onRegisterAnotherClick = {},
        onBackClick = {}
    )
}