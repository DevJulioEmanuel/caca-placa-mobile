package com.prefeitura.cacaplaca.feature.map.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import com.prefeitura.cacaplaca.core.map.CacaPlacaMapView
import com.prefeitura.cacaplaca.core.map.MapOfflineManager
import com.prefeitura.cacaplaca.feature.map.ui.components.OfflineBannerOverlay
import com.prefeitura.cacaplaca.feature.map.ui.components.PlacaDetailsBottomSheet
import com.prefeitura.cacaplaca.feature.map.ui.viewmodel.MapViewModel
import com.prefeitura.cacaplaca.ui.components.CacaPlacaHeader
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaBackground
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaNavy
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextPrimary
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextSecondary
import com.prefeitura.cacaplaca.ui.theme.OutlineGray
import org.koin.androidx.compose.koinViewModel
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
) {
    BackHandler(onBack = onBackClicked)

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var mapLibreMapRef by remember { mutableStateOf<MapLibreMap?>(null) }

    LaunchedEffect(Unit) {
        MapOfflineManager.verificarEBaixarMapaOffline(context)
        val hasFinePermission = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarsePermission = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFinePermission || hasCoarsePermission) {
            viewModel.getLocation()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CacaPlacaBackground)
    ) {
        // Header Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                CacaPlacaHeader(
                    onProfileClicked = onProfileClicked
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CacaPlacaBackground)
                            .clickable { onBackClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = CacaPlacaTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Mapa de placas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = CacaPlacaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Placas cadastradas no sistema",
                            fontSize = 13.sp,
                            color = CacaPlacaTextSecondary
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = OutlineGray.copy(alpha = 0.6f), thickness = 1.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CacaPlacaMapView(
                modifier = Modifier.fillMaxSize(),
                placas = uiState.placas,
                isLocationAvailable = uiState.isLocationAvailable,
                userLatitude = uiState.latitude,
                userLongitude = uiState.longitude,
                onPlacaSelected = { placa ->
                    viewModel.selectPlaca(placa)
                },
                onMapReady = { map ->
                    mapLibreMapRef = map
                }
            )

            OfflineBannerOverlay(
                visible = uiState.isOffline && uiState.isOfflineBannerVisible,
                onDismiss = { viewModel.dismissOfflineBanner() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(10f)
            )

            FloatingActionButton(
                onClick = {
                    if (uiState.isLocationAvailable && mapLibreMapRef != null) {
                        val userLocation = LatLng(uiState.latitude, uiState.longitude)
                        mapLibreMapRef?.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder()
                                    .target(userLocation)
                                    .zoom(16.0)
                                    .build()
                            ), 1000
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .alpha(if (uiState.isLocationAvailable) 1f else 0.5f)
                    .padding(16.dp),
                containerColor = Color.White,
                contentColor = CacaPlacaNavy
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Voltar para a minha localização"
                )
            }

            PlacaDetailsBottomSheet(
                visible = uiState.selectedPlaca != null,
                placa = uiState.selectedPlaca,
                onDismiss = { viewModel.clearSelectedPlaca() },
                onUpdateSuccess = {
                    viewModel.clearSelectedPlaca()
                    viewModel.loadPlacas()
                }
            )
        }
    }
}
