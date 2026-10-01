package com.example.cacaplaca.ui.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.koin.androidx.compose.koinViewModel
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.sources.GeoJsonOptions
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.expressions.Expression.*
import android.graphics.Color
import java.net.URI
import java.net.URISyntaxException

@Composable
fun Map(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    var mapLibreMapRef by remember { mutableStateOf<MapLibreMap?>(null) }
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

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView },
            update = { view ->
                view.getMapAsync { mapboxMap ->
                    mapLibreMapRef = mapboxMap

                    // Estilo - Online para teste inicial
                    mapboxMap.setStyle(Style.Builder().fromUri("https://basemaps.cartocdn.com/gl/positron-gl-style/style.json")) { style ->

                        try {
                            val geoJsonUri = URI("asset://points.geojson")
                            
                            style.addSource(
                                GeoJsonSource(
                                    "points-source",
                                    geoJsonUri,
                                    GeoJsonOptions()
                                        .withCluster(true)
                                        .withClusterMaxZoom(22)
                                        .withClusterRadius(50)
                                )
                            )
                            
                            // Camada de clusters agrupados (círculos)
                            style.addLayer(
                                CircleLayer("clusters", "points-source").withProperties(
                                    circleColor(
                                        step(
                                            get("point_count"),
                                            color(Color.parseColor("#51bbd6")),
                                            stop(100, color(Color.parseColor("#f1f075"))),
                                            stop(750, color(Color.parseColor("#f28cb1")))
                                        )
                                    ),
                                    circleRadius(
                                        step(
                                            get("point_count"),
                                            literal(20f),
                                            stop(100, literal(30f)),
                                            stop(750, literal(40f))
                                        )
                                    )
                                ).withFilter(has("point_count"))
                            )
                            
                            // Camada para exibir a contagem de pontos no cluster (texto)
                            style.addLayer(
                                SymbolLayer("cluster-count", "points-source").withProperties(
                                    textField(get("point_count")),
                                    textSize(12f),
                                    textColor(Color.BLACK)
                                ).withFilter(has("point_count"))
                            )
                            
                            // Camada para exibir os pontos individuais quando não estão clusterizados
                            style.addLayer(
                                CircleLayer("unclustered-points", "points-source").withProperties(
                                    circleColor(color(Color.parseColor("#11b4da"))),
                                    circleRadius(8f),
                                    circleStrokeWidth(2f),
                                    circleStrokeColor(color(Color.WHITE))
                                ).withFilter(not(has("point_count")))
                            )
                            
                        } catch (e: URISyntaxException) {
                            e.printStackTrace()
                        }

                        val quixada = LatLng(-4.9704, -39.0163)
                        mapboxMap.cameraPosition = CameraPosition.Builder()
                            .target(quixada)
                            .zoom(10.0)
                            .build()
                    }
                }
            }
        )

        // Botão de Centralizar
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
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Voltar para a minha localização"
            )
        }
    }

    // Atualiza a câmara quando o GPS responder (Executa apenas uma vez)
    LaunchedEffect(uiState.isLocationAvailable, mapLibreMapRef) {
        if (uiState.isLocationAvailable && mapLibreMapRef != null && !hasCenteredOnUser) {
            val userLocation = LatLng(uiState.latitude, uiState.longitude)
            mapLibreMapRef?.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16.0))
            hasCenteredOnUser = true
        }
    }
}