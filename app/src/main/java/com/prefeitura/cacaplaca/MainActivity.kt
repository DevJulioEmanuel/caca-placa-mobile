package com.prefeitura.cacaplaca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.prefeitura.cacaplaca.core.session.SessionManager
import com.prefeitura.cacaplaca.navigation.AppNavHost
import com.prefeitura.cacaplaca.navigation.Screen
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(applicationContext)

        setContent {
            CacaPlacaTheme {
                val startDestination = remember {
                    if (sessionManager.isLoggedIn()) Screen.Home else Screen.Auth
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                ) {
                    AppNavHost(
                        modifier = Modifier.fillMaxSize(),
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}