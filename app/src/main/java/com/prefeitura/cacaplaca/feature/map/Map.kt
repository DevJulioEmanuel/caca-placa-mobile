@file:Suppress("DEPRECATION")

package com.prefeitura.cacaplaca.feature.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.prefeitura.cacaplaca.R
import com.prefeitura.cacaplaca.ui.components.CacaPlacaHeader
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaBackground
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaNavy
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextPrimary
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextSecondary
import org.koin.androidx.compose.koinViewModel
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@Composable
fun Map(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = koinViewModel(),
    onBackClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
) {
    BackHandler(onBack = onBackClicked)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    var mapLibreMapRef by remember { mutableStateOf<MapLibreMap?>(null) }
    var userMarker by remember { mutableStateOf<Marker?>(null) }
    var hasCenteredOnUser by remember { mutableStateOf(false) }

    val mapView = remember { MapView(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            viewModel.getLocation()
        }
    }

    LaunchedEffect(Unit) {
        verificarEBaixarMapaOffline(context)
        val hasPermission = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.getLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
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

                Text(
                    text = "Mapa de placas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = CacaPlacaTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Placas cadastradas no sistema",
                    fontSize = 14.sp,
                    color = CacaPlacaTextSecondary
                )
            }
        }

        // Map Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView },
                update = { view ->
                    view.getMapAsync { mapboxMap ->
                        mapLibreMapRef = mapboxMap

                        mapboxMap.setStyle(
                            Style.Builder().fromUri(
                                "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json"
                            )
                        ) { style ->

                            val quixada = LatLng(-4.9704, -39.0163)

                            mapboxMap.cameraPosition = CameraPosition.Builder()
                                .target(quixada)
                                .zoom(14.0)
                                .build()

                            val hasFineLocation = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                            val hasCoarseLocation = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasFineLocation || hasCoarseLocation) {
                                val locationComponent = mapboxMap.locationComponent

                                locationComponent.activateLocationComponent(
                                    LocationComponentActivationOptions.builder(
                                        context,
                                        style
                                    ).build()
                                )

                                locationComponent.isLocationComponentEnabled = true
                                locationComponent.cameraMode = CameraMode.NONE
                                locationComponent.renderMode = RenderMode.NORMAL
                            }
                        }

                    }
                }
            )

            // Botão de Minha Localização
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
        }
    }

    LaunchedEffect(uiState.isLocationAvailable, uiState.latitude, uiState.longitude, mapLibreMapRef) {
        if (uiState.isLocationAvailable) {
            val userLocation = LatLng(uiState.latitude, uiState.longitude)
            mapLibreMapRef?.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16.0))
            hasCenteredOnUser = true
        }
    }
}
