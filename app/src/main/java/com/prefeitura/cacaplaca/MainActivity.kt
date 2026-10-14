package com.prefeitura.cacaplaca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTheme
import androidx.activity.enableEdgeToEdge
import com.prefeitura.cacaplaca.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CacaPlacaTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AppNavigation()
                }
            }
        }
    }
}