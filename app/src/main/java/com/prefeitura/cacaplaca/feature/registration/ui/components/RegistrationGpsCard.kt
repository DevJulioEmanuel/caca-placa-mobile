package com.prefeitura.cacaplaca.feature.registration.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prefeitura.cacaplaca.feature.registration.ui.GpsUiState
import com.prefeitura.cacaplaca.ui.theme.*

@Composable
fun RegistrationGpsCard(
    gpsState: GpsUiState,
    registrationDate: String,
    agentName: String,
    onRetryGpsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.dp, OutlineGray.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Localização",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CacaPlacaTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Badge "Automática"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(CacaPlacaOrangeLight)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Automática",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = CacaPlacaOrangeDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-bloco dinâmico do estado do GPS
            when (gpsState) {
                is GpsUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CacaPlacaBackground)
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = CacaPlacaOrange,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Buscando localização...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CacaPlacaTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Mantenha o GPS ativado. Em área aberta, o sinal pode ser mais preciso.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CacaPlacaTextSecondary
                            )
                        }
                    }
                }

                is GpsUiState.Success -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CacaPlacaBackground)
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = CacaPlacaOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Localização obtida",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CacaPlacaTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "GPS automático • Precisão de ${gpsState.accuracyMeters} m",
                                style = MaterialTheme.typography.bodySmall,
                                color = CacaPlacaTextSecondary
                            )
                            if (gpsState.isSimulated) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "SIMULADO — Não representa localização real recebida.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CacaPlacaTextSecondary
                                )
                            }
                        }
                    }
                }

                is GpsUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF2F2))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOff,
                                    contentDescription = null,
                                    tint = PinRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Localização indisponível",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PinRed
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Não foi possível obter sua posição. Verifique o GPS e a permissão de localização.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CacaPlacaTextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = onRetryGpsClick,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = SurfaceWhite,
                                    contentColor = CacaPlacaTextPrimary
                                ),
                                border = BorderStroke(1.dp, CacaPlacaOrange)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tentar novamente",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Data de cadastro • Automática",
                style = MaterialTheme.typography.bodySmall,
                color = CacaPlacaTextSecondary
            )
            Text(
                text = registrationDate,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CacaPlacaTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Agente responsável • Automático",
                style = MaterialTheme.typography.bodySmall,
                color = CacaPlacaTextSecondary
            )
            Text(
                text = agentName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CacaPlacaTextPrimary
            )
        }
    }
}