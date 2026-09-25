package com.example.cacaplaca.ui.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.alpha
import androidx.core.app.ActivityCompat
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.utsman.osmandcompose.MapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint
import org.koin.androidx.compose.koinViewModel

@Composable
fun Map(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var hasCenteredOnUser by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            viewModel.getLocation()
        }
    }

    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(0.0, 0.0)
        zoom = 16.0
    }
    val markerState = rememberMarkerState(
        geoPoint = GeoPoint(0.0, 0.0)
    )
    val locationMarkerIcon = remember {
        GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.rgb(30, 115, 255))
            setStroke(4, Color.WHITE)
            setSize(48, 48)
        }
    }

    LaunchedEffect(Unit) {
        val hasLocationPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            viewModel.getLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(uiState.isLocationAvailable) {
        if (uiState.isLocationAvailable && !hasCenteredOnUser) {
            cameraState.geoPoint = GeoPoint(uiState.latitude, uiState.longitude)
            hasCenteredOnUser = true
        }
    }

    LaunchedEffect(uiState.latitude, uiState.longitude, uiState.isLocationAvailable) {
        if (uiState.isLocationAvailable) {
            markerState.geoPoint = GeoPoint(uiState.latitude, uiState.longitude)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            properties = MapProperties(
                zoomButtonVisibility = ZoomButtonVisibility.NEVER,
            )
        ) {
            Marker(
                state = markerState,
                icon = locationMarkerIcon
            )
        }

        FloatingActionButton(
            onClick = {
                if (uiState.isLocationAvailable) {
                    cameraState.geoPoint = GeoPoint(uiState.latitude, uiState.longitude)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .alpha(if (uiState.isLocationAvailable) 1f else 0.5f)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Voltar para minha localização"
            )
        }
    }
}
