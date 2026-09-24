package com.example.cacaplaca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.preference.PreferenceManager
import com.example.cacaplaca.ui.presentation.map.Map
import com.example.cacaplaca.ui.theme.CacaPlacaTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        Configuration.getInstance().userAgentValue = packageName

        enableEdgeToEdge()
        setContent {
            CacaPlacaTheme {
                Map()
            }
        }
    }
}