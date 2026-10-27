package com.prefeitura.cacaplaca

import android.app.Application
import androidx.work.Configuration
import com.prefeitura.cacaplaca.di.appModule
import com.prefeitura.cacaplaca.di.databaseModule
import com.prefeitura.cacaplaca.di.networkModule
import com.prefeitura.cacaplaca.di.workerModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.maplibre.android.MapLibre

class CacaPlacaApplication : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()

    override fun onCreate() {
        super.onCreate()
        MapLibre.getInstance(this)
        startKoin {
            androidContext(this@CacaPlacaApplication)
            workManagerFactory()
            modules(
                appModule,
                networkModule,
                databaseModule,
                workerModule
            )
        }
    }
}
