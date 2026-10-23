package com.prefeitura.cacaplaca.feature.registration.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.prefeitura.cacaplaca.feature.registration.data.model.CapturedPhoto
import com.prefeitura.cacaplaca.ui.theme.*
import java.io.File

@Composable
fun RegistrationPhotoSection(
    modifier: Modifier = Modifier,
    photo: CapturedPhoto,
    onViewPhotoClick: () -> Unit = {},
    onRemovePhotoClick: () -> Unit,
) {
    var showFullScreenPhoto by remember { mutableStateOf(false) }

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
                AsyncImage(
                    model = File(photo.localPath),
                    contentDescription = "Foto capturada da placa",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clickable { showFullScreenPhoto = true }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showFullScreenPhoto = true
                            onViewPhotoClick()
                        },
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

    if (showFullScreenPhoto) {
        Dialog(
            onDismissRequest = { showFullScreenPhoto = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CacaPlacaNavy.copy(alpha = 0.95f))
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = File(photo.localPath),
                    contentDescription = "Visualização ampliada da placa",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )

                IconButton(
                    onClick = { showFullScreenPhoto = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = CacaPlacaTextPrimary
                    )
                }
            }
        }
    }
}
