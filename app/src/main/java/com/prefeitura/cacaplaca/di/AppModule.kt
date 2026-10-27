package com.prefeitura.cacaplaca.di

import androidx.room.Room
import androidx.work.WorkManager
import com.google.android.gms.location.LocationServices
import com.prefeitura.cacaplaca.core.gps.DefaultLocationTracker
import com.prefeitura.cacaplaca.core.gps.LocationRepository
import com.prefeitura.cacaplaca.core.gps.LocationTracker
import com.prefeitura.cacaplaca.core.gps.DefaultLocationTracker
import com.prefeitura.cacaplaca.core.network.NetworkTracker
import com.prefeitura.cacaplaca.feature.home.ui.HomeViewModel
import com.google.android.gms.location.LocationServices
import com.prefeitura.cacaplaca.core.session.SessionManager
import com.prefeitura.cacaplaca.feature.auth.AuthViewModel
import com.prefeitura.cacaplaca.feature.auth.data.AuthRepository
import com.prefeitura.cacaplaca.feature.home.ui.HomeViewModel
import com.prefeitura.cacaplaca.feature.map.MapViewModel
import com.prefeitura.cacaplaca.feature.registration.data.local.AppDatabase
import com.prefeitura.cacaplaca.feature.registration.data.repository.RegistrationRepository
import com.prefeitura.cacaplaca.feature.registration.data.worker.SyncPlacaWorker
import com.prefeitura.cacaplaca.feature.registration.ui.viewmodel.RegistrationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "cacaplaca_db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }
    single { get<AppDatabase>().placaDao() }
}

val workerModule = module {
    single { WorkManager.getInstance(androidContext()) }
    workerOf(::SyncPlacaWorker)
}

val appModule = module {
    single { LocationServices.getFusedLocationProviderClient(get<android.app.Application>()) }
    single<LocationTracker> { DefaultLocationTracker(get(), get()) }
    singleOf(::NetworkTracker)
    singleOf(::LocationRepository)
    singleOf(::SessionManager)
    singleOf(::AuthRepository)
    viewModelOf(::MapViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AuthViewModel)
    singleOf(::RegistrationRepository)
    viewModelOf(::RegistrationViewModel)
}
