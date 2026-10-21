package com.prefeitura.cacaplaca.feature.registration.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prefeitura.cacaplaca.feature.registration.data.CapturedPhoto
import com.prefeitura.cacaplaca.ui.theme.*

@Composable
fun RegistrationPhotoSection(
    photo: CapturedPhoto,
    onViewPhotoClick: () -> Unit,
    onRemovePhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Foto do cadastro",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = CacaPlacaTextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "1 foto adicionada",
            style = MaterialTheme.typography.bodySmall,
            color = CacaPlacaTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = SurfaceWhite,
            border = BorderStroke(1.5.dp, CacaPlacaOrange)
        ) {
            Column {
                // Placeholder da foto (No futuro, usar AsyncImage do Coil com photo.localPath)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(OutlineGray.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = null,
                            tint = CacaPlacaTextSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Foto capturada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CacaPlacaTextSecondary
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewPhotoClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, OutlineGray)
                    ) {
                        Text(
                            text = "Ver",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = CacaPlacaTextPrimary
                        )
                    }

                    OutlinedButton(
                        onClick = onRemovePhotoClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, OutlineGray)
                    ) {
                        Text(
                            text = "Remover",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = CacaPlacaTextPrimary
                        )
                    }
                }
            }
        }
    }
}