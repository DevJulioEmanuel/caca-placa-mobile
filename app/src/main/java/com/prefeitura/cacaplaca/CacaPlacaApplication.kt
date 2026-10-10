package com.prefeitura.cacaplaca

import android.app.Application
import com.prefeitura.cacaplaca.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.maplibre.android.MapLibre

class CacaPlacaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        MapLibre.getInstance(this)

        startKoin {
            androidContext(this@CacaPlacaApplication)
            modules(appModule)
        }
    }
}