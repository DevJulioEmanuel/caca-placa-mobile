package com.example.cacaplaca

import android.app.Application
import com.example.cacaplaca.data.map.LocationRepository
import com.example.cacaplaca.ui.presentation.map.MapViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val appModule = module {
    singleOf(::LocationRepository)
    viewModelOf(::MapViewModel)
}

class CacaPlacaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CacaPlacaApplication)
            modules(appModule)
        }
    }
}
