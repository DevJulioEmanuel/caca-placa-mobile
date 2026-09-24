package com.example.cacaplaca.ui.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cacaplaca.ui.theme.CacaPlacaTheme
import com.utsman.osmandcompose.MapProperties
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import org.osmdroid.util.GeoPoint

@Composable
fun Map(modifier: Modifier = Modifier) {

    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(-6.3970066, 106.8224316)
        zoom = 12.0
    }

    OpenStreetMap(
        modifier = modifier.fillMaxSize(),
        cameraState = cameraState,
        properties = MapProperties(
            zoomButtonVisibility = ZoomButtonVisibility.NEVER,
        )
    )
}

@Preview
@Composable
private fun MapPreview() {
    CacaPlacaTheme {
        Map()
    }
}