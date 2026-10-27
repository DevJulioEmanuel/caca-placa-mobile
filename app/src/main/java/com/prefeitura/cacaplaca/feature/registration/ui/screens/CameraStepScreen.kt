package com.prefeitura.cacaplaca.feature.registration.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Refresh
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.prefeitura.cacaplaca.feature.registration.ui.components.RegistrationTopBar
import com.prefeitura.cacaplaca.ui.theme.*
import java.io.File

@Composable
fun CameraStepContent(
    capturedPhotoPath: String?,
    onPhotoCaptured: (String) -> Unit,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
    }

    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    var isCapturing by remember { mutableStateOf(false) }
    val hasPhoto = capturedPhotoPath != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CacaPlacaBackground)
    ) {
        RegistrationTopBar(
            title = "Fotografar placa",
            subtitle = "Etapa 1 de 2",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = "Enquadre a placa inteira na foto",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = CacaPlacaTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Use a câmera para registrar a placa e o entorno. Sem galeria.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = CacaPlacaTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CacaPlacaNavy)
            ) {
                when {
                    hasPhoto -> {
                        AsyncImage(
                            model = File(capturedPhotoPath!!),
                            contentDescription = "Foto da placa",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    hasPermissions -> {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { ctx ->
                                val previewView = PreviewView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    scaleType = PreviewView.ScaleType.FILL_CENTER
                                }

                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.surfaceProvider = previewView.surfaceProvider
                                    }

                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            CameraSelector.DEFAULT_BACK_CAMERA,
                                            preview,
                                            imageCapture
                                        )
                                    } catch (e: Exception) {
                                        Log.e("CameraStep", "Erro ao iniciar CameraX", e)
                                    }
                                }, ContextCompat.getMainExecutor(ctx))

                                previewView
                            }
                        )
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Permissões de câmera e localização necessárias.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SurfaceWhite,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { 
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    ) 
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CacaPlacaOrange)
                            ) {
                                Text("Permitir Acesso", color = SurfaceWhite)
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp)
                        .clip(RoundedCornerShape(50))
                        .background(CacaPlacaNavy.copy(alpha = 0.65f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (hasPhoto) "Foto capturada ✓" else "Câmera ativa",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = SurfaceWhite
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (hasPhoto) {
                            "Toque no botão para tirar outra ou clique em Continuar"
                        } else {
                            "Toque no botão para fotografar"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = SurfaceWhite
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .clickable(enabled = hasPermissions && !isCapturing) {
                                if (hasPhoto) {
                                    onPhotoCaptured("")
                                } else {
                                    isCapturing = true
                                    takePhoto(
                                        context = context,
                                        imageCapture = imageCapture,
                                        onPhotoSaved = { file ->
                                            isCapturing = false
                                            onPhotoCaptured(file.absolutePath)
                                        },
                                        onError = {
                                            isCapturing = false
                                        }
                                    )
                                }
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(CacaPlacaOrangeLight),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCapturing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(26.dp),
                                    color = CacaPlacaOrange,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (hasPhoto) Icons.Outlined.Refresh else Icons.Outlined.PhotoCamera,
                                    contentDescription = "Fotografar",
                                    tint = CacaPlacaOrange,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onContinueClick,
                enabled = hasPhoto,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CacaPlacaOrange,
                    contentColor = CacaPlacaTextPrimary,
                    disabledContainerColor = SurfaceWhite,
                    disabledContentColor = CacaPlacaTextSecondary
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (hasPhoto) CacaPlacaOrange else OutlineGray
                )
            ) {
                Text(
                    text = "Continuar",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoSaved: (File) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = File(
        context.cacheDir,
        "placa_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onPhotoSaved(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraStep", "Falha ao salvar foto: ${exception.message}", exception)
                onError(exception)
            }
        }
    )
}
