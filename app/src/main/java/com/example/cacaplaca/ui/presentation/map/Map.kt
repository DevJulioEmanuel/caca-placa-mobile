package com.example.cacaplaca.ui.presentation.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.rendertheme.InternalRenderTheme
import org.osmdroid.config.Configuration
import org.osmdroid.mapsforge.MapsForgeTileProvider
import org.osmdroid.mapsforge.MapsForgeTileSource
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.io.FileOutputStream

@Composable
fun Map(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var hasCenteredOnUser by remember { mutableStateOf(false) }
    var mapsForgeTileSource by remember { mutableStateOf<MapsForgeTileSource?>(null) }
    var isLoadingMap by remember { mutableStateOf(true) }

    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var locationMarkerRef by remember { mutableStateOf<Marker?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            viewModel.getLocation()
        }
    }

    val locationMarkerIcon = remember {
        GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.rgb(30, 115, 255))
            setStroke(4, Color.WHITE)
            setSize(48, 48)
        }
    }

    Configuration.getInstance().userAgentValue = context.packageName
    Configuration.getInstance().osmdroidBasePath = File(context.cacheDir, "osmdroid")
    Configuration.getInstance().osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")

    LaunchedEffect(Unit) {
        AndroidGraphicFactory.createInstance(context.applicationContext)

        val mapFile = File(context.filesDir, "quixada-ceara.map")

        if (!mapFile.exists() || mapFile.length() == 0L) {
            copyAssetToFile(context, "quixada-ceara.map", mapFile)
        }

        if (mapFile.exists() && mapFile.length() > 0) {
            try {
                mapsForgeTileSource = MapsForgeTileSource.createFromFiles(
                    arrayOf(mapFile),
                    InternalRenderTheme.DEFAULT,
                    "TemaQuixada"
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        isLoadingMap = false

        val hasLocationPermission = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            viewModel.getLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isLoadingMap) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (mapsForgeTileSource != null) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val forgeProvider = MapsForgeTileProvider(
                        SimpleRegisterReceiver(ctx),
                        mapsForgeTileSource,
                        null
                    )
                    MapView(ctx, forgeProvider).apply {
                        setUseDataConnection(false)
                        setMultiTouchControls(true)
                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

                        // Define o centro inicial em Quixadá enquanto o GPS não responde
                        val quixada = GeoPoint(-4.9704, -39.0163)
                        controller.setZoom(15.0)
                        controller.setCenter(quixada)

                        locationMarkerRef = Marker(this).apply {
                            icon = locationMarkerIcon
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        }
                        overlays.add(locationMarkerRef)

                        mapViewRef = this
                    }
                },
                update = { view ->
                    if (view.tileProvider.tileSource != mapsForgeTileSource) {
                        view.setTileSource(mapsForgeTileSource)
                    }

                    if (uiState.isLocationAvailable) {
                        val userGeoPoint = GeoPoint(uiState.latitude, uiState.longitude)
                        locationMarkerRef?.position = userGeoPoint

                        if (!hasCenteredOnUser) {
                            view.controller.setZoom(16.0)
                            view.controller.setCenter(userGeoPoint)
                            hasCenteredOnUser = true
                        }
                        view.invalidate()
                    }
                }
            )
        } else {
            Text("Erro ao carregar o mapa offline.", modifier = Modifier.align(Alignment.Center))
        }

        FloatingActionButton(
            onClick = {
                if (uiState.isLocationAvailable) {
                    mapViewRef?.controller?.animateTo(GeoPoint(uiState.latitude, uiState.longitude))
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
}

suspend fun copyAssetToFile(context: Context, assetName: String, destinationFile: File) {
    withContext(Dispatchers.IO) {
        try {
            context.assets.open(assetName).use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (destinationFile.exists()) {
                destinationFile.delete()
            }
        }
    }
}