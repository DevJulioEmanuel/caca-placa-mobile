@file:Suppress("DEPRECATION")

package com.prefeitura.cacaplaca.feature.map.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.prefeitura.cacaplaca.R
import com.prefeitura.cacaplaca.feature.map.MapViewModel
import com.prefeitura.cacaplaca.feature.map.data.verificarEBaixarMapaOffline
import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse
import com.prefeitura.cacaplaca.ui.components.CacaPlacaHeader
import com.prefeitura.cacaplaca.ui.components.OfflineBanner
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaBackground
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaNavy
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextPrimary
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextSecondary
import com.prefeitura.cacaplaca.ui.theme.OutlineGray
import org.koin.androidx.compose.koinViewModel
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions
import kotlin.math.sqrt

private fun getBitmapFromDrawable(context: Context, @DrawableRes drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
    val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 100
    val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 100
    val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

private data class ClusterData(
    val center: LatLng,
    val placas: List<PlacaItemResponse>
)

private fun updateMarkersAndClusters(
    mapboxMap: MapLibreMap?,
    symbolManager: SymbolManager?,
    placas: List<PlacaItemResponse>
) {
    if (mapboxMap == null || symbolManager == null) return

    symbolManager.deleteAll()

    if (placas.isEmpty()) return

    val projection = mapboxMap.projection
    val clusterRadiusPixels = 60f

    val visited = mutableSetOf<Int>()
    val clusters = mutableListOf<ClusterData>()

    for (i in placas.indices) {
        if (i in visited) continue
        val p1 = placas[i]
        val screenPoint1 = projection.toScreenLocation(LatLng(p1.latitude, p1.longitude))

        val clusterPlacas = mutableListOf(p1)
        visited.add(i)

        for (j in (i + 1) until placas.size) {
            if (j in visited) continue
            val p2 = placas[j]
            val screenPoint2 = projection.toScreenLocation(LatLng(p2.latitude, p2.longitude))

            val dx = screenPoint1.x - screenPoint2.x
            val dy = screenPoint1.y - screenPoint2.y
            val distance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            if (distance <= clusterRadiusPixels) {
                clusterPlacas.add(p2)
                visited.add(j)
            }
        }

        val avgLat = clusterPlacas.map { it.latitude }.average()
        val avgLng = clusterPlacas.map { it.longitude }.average()
        val centerLatLng = LatLng(avgLat, avgLng)

        clusters.add(ClusterData(centerLatLng, clusterPlacas))
    }

    clusters.forEach { cluster ->
        if (cluster.placas.size == 1) {
            val placa = cluster.placas[0]
            val iconName = if (placa.codigoContran.startsWith("R", ignoreCase = true)) {
                "icon_red"
            } else {
                "icon_yellow"
            }
            val symbolOptions = SymbolOptions()
                .withLatLng(cluster.center)
                .withIconImage(iconName)
                .withIconSize(0.25f)
            symbolManager.create(symbolOptions)
        } else {
            val count = cluster.placas.size
            val symbolOptions = SymbolOptions()
                .withLatLng(cluster.center)
                .withIconImage("icon_cluster")
                .withIconSize(0.25f)
                .withTextField(count.toString())
                .withTextSize(14f)
                .withTextColor("#FFFFFF")
                .withTextAnchor("center")
                .withIconAnchor("center")
                .withTextOffset(arrayOf(0f, -0.3f))
                .withTextFont(arrayOf("Open Sans Bold", "Arial Unicode MS Bold"))
            symbolManager.create(symbolOptions)
        }
    }
}

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
    var symbolManagerRef by remember { mutableStateOf<SymbolManager?>(null) }

    val mapView = remember { MapView(context) }

    LaunchedEffect(Unit) {
        verificarEBaixarMapaOffline(context)
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

                            // Add icons to style
                            val redBitmap = getBitmapFromDrawable(context, R.drawable.red)
                            val yellowBitmap = getBitmapFromDrawable(context, R.drawable.yellow)
                            val clusterBitmap = getBitmapFromDrawable(context, R.drawable.cluster)
                            if (redBitmap != null) style.addImage("icon_red", redBitmap)
                            if (yellowBitmap != null) style.addImage("icon_yellow", yellowBitmap)
                            if (clusterBitmap != null) style.addImage("icon_cluster", clusterBitmap)

                            val symbolManager = SymbolManager(mapView, mapboxMap, style).apply {
                                iconAllowOverlap = true
                                textAllowOverlap = true
                            }
                            symbolManagerRef = symbolManager

                            mapboxMap.addOnCameraIdleListener {
                                updateMarkersAndClusters(mapboxMap, symbolManager, viewModel.uiState.value.placas)
                            }

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
        }
    }

    LaunchedEffect(uiState.isLocationAvailable, uiState.latitude, uiState.longitude, mapLibreMapRef) {
        if (uiState.isLocationAvailable) {
            val userLocation = LatLng(uiState.latitude, uiState.longitude)
            mapLibreMapRef?.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16.0))
        }
    }

    LaunchedEffect(uiState.placas, symbolManagerRef) {
        val map = mapLibreMapRef
        val manager = symbolManagerRef
        if (map != null && manager != null) {
            updateMarkersAndClusters(map, manager, uiState.placas)
        }
    }
}

@Composable
private fun OfflineBannerOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        OfflineBanner(onDismiss = onDismiss)
    }
}
