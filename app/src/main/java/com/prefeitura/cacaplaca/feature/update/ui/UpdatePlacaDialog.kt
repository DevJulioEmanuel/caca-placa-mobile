package com.prefeitura.cacaplaca.feature.update.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse
import com.prefeitura.cacaplaca.core.model.TipoPelicula
import com.prefeitura.cacaplaca.core.model.TipoSubstrato
import com.prefeitura.cacaplaca.core.model.TipoSuporte
import com.prefeitura.cacaplaca.core.model.availablePlatesList
import com.prefeitura.cacaplaca.ui.components.PlateSelectionContent
import com.prefeitura.cacaplaca.feature.update.UpdatePlacaViewModel
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaNavy
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrange
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

private fun formatEnumText(text: String): String {
    if (text.isEmpty()) return ""
    return text.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}

@Composable
fun UpdatePlacaDialog(
    placa: PlacaItemResponse,
    onDismiss: () -> Unit,
    onUpdateSuccess: () -> Unit,
    viewModel: UpdatePlacaViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val tempSelectedPlate by viewModel.tempSelectedPlate.collectAsState()
    val context = LocalContext.current

    var showPlateSelection by remember { mutableStateOf(false) }

    LaunchedEffect(placa) {
        viewModel.initData(placa)
    }

    if (uiState.isSuccess) {
        SuccessDialog(
            codigoPlaca = uiState.codigoPlaca,
            onReturn = {
                viewModel.resetState()
                onUpdateSuccess()
            }
        )
    } else if (showPlateSelection) {
        Dialog(
            onDismissRequest = {
                viewModel.resetPlateSelection()
                showPlateSelection = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            PlateSelectionContent(
                title = "Alterar código da placa",
                subtitle = "Edição de dados cadastrais",
                query = searchQuery,
                onQueryChanged = viewModel::onSearchQueryChanged,
                availablePlates = availablePlatesList,
                tempSelectedPlate = tempSelectedPlate,
                onTempPlateSelected = viewModel::onTempPlateSelected,
                onConfirmSelection = {
                    viewModel.confirmPlateSelection()
                    showPlateSelection = false
                },
                onBackClick = {
                    viewModel.resetPlateSelection()
                    showPlateSelection = false
                }
            )
        }
    } else {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .wrapContentHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Editar Dados da placa: ${uiState.codigoPlaca}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CacaPlacaNavy
                            )
                            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = CacaPlacaNavy
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        PhotoSection(
                            photoUri = uiState.photoUri,
                            onPhotoSelected = viewModel::onPhotoSelected
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Código da placa:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CacaPlacaNavy)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedButton(
                                onClick = { showPlateSelection = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                border = BorderStroke(1.dp, Color.Gray)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        text = uiState.codigoPlaca,
                                        color = Color.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ReadOnlyField(label = "Localização:", value = uiState.localizacao)

                        Spacer(modifier = Modifier.height(12.dp))

                        DropdownField(
                            label = "Tipo de Película:",
                            selectedValue = uiState.tipoPelicula,
                            options = TipoPelicula.values().map { it.name },
                            onValueChange = viewModel::onTipoPeliculaChanged
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        DropdownField(
                            label = "Tipo de Substrato:",
                            selectedValue = uiState.tipoSubstrato,
                            options = TipoSubstrato.values().map { it.name },
                            onValueChange = viewModel::onTipoSubstratoChanged
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        DropdownField(
                            label = "Tipo de Suporte:",
                            selectedValue = uiState.tipoSuporte,
                            options = TipoSuporte.values().map { it.name },
                            onValueChange = viewModel::onTipoSuporteChanged
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (!uiState.errorMessage.isNullOrEmpty()) {
                            Surface(
                                color = Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = uiState.errorMessage!!,
                                    color = Color(0xFFD32F2F),
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Button(
                            onClick = {
                                val file = uiState.photoUri?.let { uriToFile(context, it) }
                                viewModel.submitUpdate(file)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CacaPlacaNavy),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(text = "Salvar alterações", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, CacaPlacaNavy),
                            enabled = !uiState.isLoading
                        ) {
                            Text(text = "Cancelar", color = CacaPlacaNavy, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoSection(
    photoUri: Uri?,
    onPhotoSelected: (Uri?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        onPhotoSelected(uri)
    }

    val launchGallery = {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                .clickable { launchGallery() }
                .padding(if (photoUri == null) 16.dp else 0.dp),
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Ícone na caixa vazia
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = CacaPlacaOrange,
                    modifier = Modifier.size(48.dp) // Ícone também maior
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = "Foto Cadastrada", fontWeight = FontWeight.Bold, color = CacaPlacaNavy)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = launchGallery,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Atualizar Foto", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CacaPlacaNavy)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                .padding(12.dp)
        ) {
            Text(text = value, fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CacaPlacaNavy)
        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = if (selectedValue.isNotEmpty()) formatEnumText(selectedValue) else "Selecionar",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { backendValue ->
                    DropdownMenuItem(
                        text = { Text(formatEnumText(backendValue)) },
                        onClick = {
                            onValueChange(backendValue)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuccessDialog(codigoPlaca: String, onReturn: () -> Unit) {
    Dialog(onDismissRequest = onReturn) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Alterações Salvas!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = CacaPlacaNavy
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Os dados da placa $codigoPlaca foram salvos e já estão disponíveis para visualização.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onReturn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CacaPlacaOrange)
                ) {
                    Text(text = "RETORNAR", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"

        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload_", ".$extension", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)

        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        null
    }
}