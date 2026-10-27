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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import com.prefeitura.cacaplaca.R
import com.prefeitura.cacaplaca.feature.map.MapViewModel
import com.prefeitura.cacaplaca.feature.map.data.verificarEBaixarMapaOffline
import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse
import com.prefeitura.cacaplaca.ui.components.CacaPlacaHeader
import com.prefeitura.cacaplaca.ui.components.OfflineBanner
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaBackground
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaNavy
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrange
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrangeDark
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrangeLight
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
import java.util.Locale
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

private fun getPlateDescription(code: String): String {
    return when (code.uppercase()) {
        "R-1" -> "Parada obrigatória"
        "R-2" -> "Dê a preferência"
        "R-6A", "R-6a" -> "Proibido estacionar"
        else -> "Sinalização $code"
    }
}

private fun formatEnumText(text: String?): String {
    if (text.isNullOrEmpty()) return "Não informado"
    return text.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}

private fun updateMarkersAndClusters(
    mapboxMap: MapLibreMap?,
    symbolManager: SymbolManager?,
    placas: List<PlacaItemResponse>,
    symbolToPlacaMap: MutableMap<Long, PlacaItemResponse>
) {
    if (mapboxMap == null || symbolManager == null) return

    symbolManager.deleteAll()
    symbolToPlacaMap.clear()

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
            val symbol = symbolManager.create(symbolOptions)
            symbolToPlacaMap[symbol.id] = placa
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
    val symbolToPlacaMap = remember { mutableMapOf<Long, PlacaItemResponse>() }

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

                            symbolManager.addClickListener { symbol ->
                                val placa = symbolToPlacaMap[symbol.id]
                                if (placa != null) {
                                    viewModel.selectPlaca(placa)
                                    true
                                } else {
                                    val latLng = symbol.latLng
                                    if (latLng != null) {
                                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, mapboxMap.cameraPosition.zoom + 2))
                                    }
                                    true
                                }
                            }

                            mapboxMap.addOnCameraIdleListener {
                                updateMarkersAndClusters(mapboxMap, symbolManager, viewModel.uiState.value.placas, symbolToPlacaMap)
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

            // Placa Details Bottom Sheet Card with slide-in/out animation
            PlacaDetailsCardOverlay(
                visible = uiState.selectedPlaca != null,
                placa = uiState.selectedPlaca,
                onDismiss = { viewModel.clearSelectedPlaca() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(20f)
                    .fillMaxWidth()
            )
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
            updateMarkersAndClusters(map, manager, uiState.placas, symbolToPlacaMap)
        }
    }
}

@Composable
private fun PlacaDetailsCardOverlay(
    visible: Boolean,
    placa: PlacaItemResponse?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        if (placa != null) {
            PlacaDetailsCard(
                placa = placa,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun PlacaDetailsCard(
    placa: PlacaItemResponse,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember(placa.id) { mutableStateOf(false) }
    var selectedTab by remember(placa.id) { mutableStateOf(0) } // 0: Situação atual, 1: Histórico

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OutlineGray)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title and Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = getPlateDescription(placa.codigoContran),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    color = CacaPlacaTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = CacaPlacaTextPrimary
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                // Tabs: Situação atual | Histórico
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .clickable { selectedTab = 0 }
                            .padding(bottom = 8.dp, end = 24.dp)
                    ) {
                        Text(
                            text = "Situação atual",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedTab == 0) CacaPlacaOrange else CacaPlacaTextSecondary
                        )
                        if (selectedTab == 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(3.dp)
                                    .background(CacaPlacaOrange, RoundedCornerShape(1.5.dp))
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .clickable { selectedTab = 1 }
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Histórico",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedTab == 1) CacaPlacaOrange else CacaPlacaTextSecondary
                        )
                        if (selectedTab == 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(3.dp)
                                    .background(CacaPlacaOrange, RoundedCornerShape(1.5.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // Middle section: Photo + Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Photo thumbnail
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CacaPlacaBackground)
                    ) {
                        val fotoUrl = placa.ultimoEvento?.fotoUrl
                        if (!fotoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = fotoUrl,
                                contentDescription = "Foto da placa",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = CacaPlacaTextSecondary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${placa.codigoContran} • ${placa.id?.take(8)?.uppercase() ?: "---"}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = CacaPlacaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        if (isExpanded) {
                            val estadoGeral = placa.ultimoEvento?.estadoConservacao ?: "REGULAR"
                            val geralText = when (estadoGeral.uppercase()) {
                                "BOM" -> "Bom"
                                "REGULAR" -> "Regular"
                                "RUIM" -> "Ruim"
                                else -> estadoGeral
                            }
                            Text(
                                text = "Conservação geral: $geralText",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = CacaPlacaOrange
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        }

                        val dateStr = placa.createdAt?.take(10)?.split("-")?.reversed()?.joinToString("/") ?: "N/A"
                        Text(
                            text = "Registro: $dateStr",
                            style = MaterialTheme.typography.bodySmall,
                            color = CacaPlacaTextSecondary
                        )

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(2.dp))
                            val createdBy = placa.ultimoEvento?.createdBy?.let { "Agente #$it" } ?: "Agente Sistema"
                            Text(
                                text = "Responsável: $createdBy",
                                style = MaterialTheme.typography.bodySmall,
                                color = CacaPlacaTextSecondary
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            // Situação Badge for collapsed view
                            val estado = placa.ultimoEvento?.estadoConservacao ?: "REGULAR"
                            val situacaoText = when (estado.uppercase()) {
                                "BOM" -> "Situação: Bom"
                                "REGULAR" -> "Situação: Regular"
                                "RUIM" -> "Situação: Ruim"
                                else -> "Situação: $estado"
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CacaPlacaOrangeLight
                            ) {
                                Text(
                                    text = situacaoText,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = CacaPlacaOrangeDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Address section
                val logradouro = placa.endereco?.logradouro ?: "Endereço não informado"
                val bairro = placa.endereco?.bairro
                val addressDisplay = if (!bairro.isNullOrEmpty()) {
                    if (isExpanded) "$logradouro • $bairro • Quixadá, CE" else "$logradouro • $bairro"
                } else {
                    logradouro
                }

                if (isExpanded) {
                    Text(
                        text = "Localização",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = addressDisplay,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CacaPlacaTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isExpanded) {
                        "Latitude ${String.format(Locale.US, "%.4f", placa.latitude)} • Longitude ${String.format(Locale.US, "%.4f", placa.longitude)}"
                    } else {
                        "Quixadá, CE • ${String.format(Locale.US, "%.4f", placa.latitude)}, ${String.format(Locale.US, "%.4f", placa.longitude)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = CacaPlacaTextSecondary
                )

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Película card
                    InfoAttributeCard(
                        title = "Tipo de película",
                        value = formatEnumText(placa.tipoPelicula),
                        statusLabel = "Conservação da película",
                        statusValue = formatEnumText(placa.ultimoEvento?.estadoPelicula)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Substrato card
                    InfoAttributeCard(
                        title = "Tipo de substrato",
                        value = formatEnumText(placa.tipoSubstrato),
                        statusLabel = "Conservação do substrato",
                        statusValue = formatEnumText(placa.ultimoEvento?.estadoSubstrato)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Suporte card
                    InfoAttributeCard(
                        title = "Tipo de suporte",
                        value = formatEnumText(placa.tipoSuporte),
                        statusLabel = "Conservação do suporte",
                        statusValue = formatEnumText(placa.ultimoEvento?.estadoSuporte)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Observações
                    Text(
                        text = "Observações",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = placa.ultimoEvento?.observacao ?: "Nenhuma observação registrada para esta placa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CacaPlacaTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (!isExpanded) {
                    // Action Buttons (Collapsed state)
                    Button(
                        onClick = { isExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CacaPlacaOrange)
                    ) {
                        Text(
                            text = "Ver informações completas",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { /* New report action */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CacaPlacaOrange),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CacaPlacaOrange)
                    ) {
                        Text(
                            text = "Cadastrar novo relatório",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = CacaPlacaOrange
                        )
                    }
                }
            } else {
                // Histórico tab content
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Histórico de eventos",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CacaPlacaTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CacaPlacaBackground
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = placa.ultimoEvento?.tipoEvento ?: "Criação",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = CacaPlacaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val ts = placa.ultimoEvento?.timestamp?.take(10)?.split("-")?.reversed()?.joinToString("/") ?: placa.createdAt?.take(10) ?: ""
                        Text(
                            text = "Data: $ts",
                            style = MaterialTheme.typography.bodySmall,
                            color = CacaPlacaTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Estado: ${placa.ultimoEvento?.estadoConservacao ?: "N/A"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CacaPlacaTextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun InfoAttributeCard(
    title: String,
    value: String,
    statusLabel: String,
    statusValue: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CacaPlacaBackground,
        border = BorderStroke(1.dp, OutlineGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = CacaPlacaTextSecondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CacaPlacaTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = OutlineGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = CacaPlacaTextSecondary
                )
                Text(
                    text = statusValue,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CacaPlacaOrange
                )
            }
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
