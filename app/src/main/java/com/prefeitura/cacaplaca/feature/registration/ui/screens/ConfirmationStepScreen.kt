package com.prefeitura.cacaplaca.feature.registration.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.prefeitura.cacaplaca.feature.registration.ui.components.RegistrationTopBar
import com.prefeitura.cacaplaca.feature.registration.ui.state.*
import com.prefeitura.cacaplaca.ui.theme.*
import java.io.File

@Composable
fun ConfirmationStepContent(
    status: ConfirmationSyncStatus,
    plateLabel: String,
    photoPath: String?,
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
            .background(CacaPlacaBackground)
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceWhite,
                border = BorderStroke(1.dp, OutlineGray.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(CacaPlacaOrangeLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = null,
                            tint = CacaPlacaOrange,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isSynced) "Placa cadastrada com sucesso!" else "Placa salva no dispositivo",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                        color = CacaPlacaTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isSynced) {
                            "O cadastro foi enviado ao servidor e já está disponível no mapa."
                        } else {
                            "Você está sem conexão. O cadastro e a foto foram salvos neste aparelho."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = CacaPlacaTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CacaPlacaNavy)
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isSynced) Icons.Outlined.CloudDone else Icons.Outlined.CloudOff,
                                    contentDescription = null,
                                    tint = SurfaceWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isSynced) "Sincronizado" else "Aguardando sincronização",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = SurfaceWhite
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isSynced) {
                                    "O cadastro foi confirmado pelo servidor."
                                } else {
                                    "O cadastro será sincronizado automaticamente quando houver conexão."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                color = SurfaceWhite.copy(alpha = 0.9f)
                            )
                        }
                    }

                    if (isSynced) {
                        Spacer(modifier = Modifier.height(16.dp))
                        PlateSummaryBox(
                            plateLabel = plateLabel,
                            photoPath = photoPath,
                            agentName = agentName,
                            date = date,
                            showPhotoPreview = true
                        )
                    } else {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Ainda não enviado ao servidor. Você pode continuar trabalhando.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CacaPlacaTextSecondary
                        )
                    }
                }
            }

            if (!isSynced) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceWhite,
                    border = BorderStroke(1.dp, OutlineGray.copy(alpha = 0.7f))
                ) {
                    PlateSummaryBox(
                        plateLabel = plateLabel,
                        photoPath = photoPath,
                        agentName = agentName,
                        date = date,
                        showPhotoPreview = false,
                        hasBorder = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onViewOnMapClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CacaPlacaOrange,
                    contentColor = CacaPlacaTextPrimary
                )
            ) {
                Text(
                    text = "Ver no mapa",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRegisterAnotherClick,
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
                    text = "Cadastrar outra placa",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun PlateSummaryBox(
    plateLabel: String,
    photoPath: String?,
    agentName: String,
    date: String,
    showPhotoPreview: Boolean,
    hasBorder: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (hasBorder) CacaPlacaBackground else SurfaceWhite,
        border = if (hasBorder) BorderStroke(1.dp, OutlineGray.copy(alpha = 0.7f)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = plateLabel,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = CacaPlacaTextPrimary
            )

            if (showPhotoPreview && !photoPath.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = File(photoPath),
                    contentDescription = "Foto da placa cadastrada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "1 foto • Localização obtida",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = CacaPlacaTextSecondary
            )
            Text(
                text = agentName,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = CacaPlacaTextSecondary
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = CacaPlacaTextSecondary
            )
        }
    }
}
