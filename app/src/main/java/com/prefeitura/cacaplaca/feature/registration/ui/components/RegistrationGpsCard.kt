package com.prefeitura.cacaplaca.feature.registration.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prefeitura.cacaplaca.feature.registration.ui.GpsUiState
import com.prefeitura.cacaplaca.ui.theme.*

@Composable
fun RegistrationGpsCard(
    gpsState: GpsUiState,
    registrationDate: String,
    agentName: String,
    onRetryGpsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.dp, OutlineGray)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = CacaPlacaOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Localização",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CacaPlacaTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (gpsState) {
                is GpsUiState.Success -> {
                    Text(
                        text = "Lat: ${gpsState.latitude} | Lng: ${gpsState.longitude}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CacaPlacaTextPrimary
                    )
                }
                is GpsUiState.Error -> {
                    Text(
                        text = "Falha ao obter GPS.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                GpsUiState.Loading -> {
                    Text(
                        text = "Obtendo localização...",
                        style = MaterialTheme.typography.bodySmall,
                        color = CacaPlacaTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Data: $registrationDate",
                style = MaterialTheme.typography.bodySmall,
                color = CacaPlacaTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Agente: $agentName",
                style = MaterialTheme.typography.bodySmall,
                color = CacaPlacaTextSecondary
            )
        }
    }
}