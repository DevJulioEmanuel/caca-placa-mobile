package com.prefeitura.cacaplaca.core.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.prefeitura.cacaplaca.R
import com.prefeitura.cacaplaca.feature.map.data.model.PlacaUiModel
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

private data class ClusterData(
    val center: LatLng,
    val placas: List<PlacaUiModel>
)

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

private fun updateMarkersAndClusters(
    mapboxMap: MapLibreMap?,
    symbolManager: SymbolManager?,
    placas: List<PlacaUiModel>,
    symbolToPlacaMap: MutableMap<Long, PlacaUiModel>
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
fun CacaPlacaMapView(
    modifier: Modifier = Modifier,
    placas: List<PlacaUiModel>,
    isLocationAvailable: Boolean,
    userLatitude: Double,
    userLongitude: Double,
    onPlacaSelected: (PlacaUiModel) -> Unit,
    onMapReady: (MapLibreMap) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var mapLibreMapRef by remember { mutableStateOf<MapLibreMap?>(null) }
    var symbolManagerRef by remember { mutableStateOf<SymbolManager?>(null) }
    val symbolToPlacaMap = remember { mutableMapOf<Long, PlacaUiModel>() }

    val mapView = remember { MapView(context) }

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

    val currentPlacas by rememberUpdatedState(placas)
    val currentOnPlacaSelected by rememberUpdatedState(onPlacaSelected)

    AndroidView(
        modifier = modifier,
        factory = {
            mapView.apply {
                getMapAsync { mapboxMap ->
                    mapLibreMapRef = mapboxMap
                    onMapReady(mapboxMap)

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

                        val redBitmap = getBitmapFromDrawable(context, R.drawable.red)
                        val yellowBitmap = getBitmapFromDrawable(context, R.drawable.yellow)
                        val clusterBitmap = getBitmapFromDrawable(context, R.drawable.cluster)
                        if (redBitmap != null) style.addImage("icon_red", redBitmap)
                        if (yellowBitmap != null) style.addImage("icon_yellow", yellowBitmap)
                        if (clusterBitmap != null) style.addImage("icon_cluster", clusterBitmap)

                        val symbolManager = SymbolManager(this, mapboxMap, style).apply {
                            iconAllowOverlap = true
                            textAllowOverlap = true
                        }
                        symbolManagerRef = symbolManager

                        symbolManager.addClickListener { symbol ->
                            val placa = symbolToPlacaMap[symbol.id]
                            if (placa != null) {
                                currentOnPlacaSelected(placa)
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
                            updateMarkersAndClusters(mapboxMap, symbolManager, currentPlacas, symbolToPlacaMap)
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
        },
        update = {
            // Nothing here to avoid recreating the map!
        }
    )

    LaunchedEffect(isLocationAvailable, userLatitude, userLongitude, mapLibreMapRef) {
        if (isLocationAvailable) {
            val userLocation = LatLng(userLatitude, userLongitude)
            mapLibreMapRef?.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16.0))
        }
    }

    LaunchedEffect(placas, symbolManagerRef) {
        val map = mapLibreMapRef
        val manager = symbolManagerRef
        if (map != null && manager != null) {
            updateMarkersAndClusters(map, manager, placas, symbolToPlacaMap)
        }
    }
}
