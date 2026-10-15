package com.prefeitura.cacaplaca.di

import com.prefeitura.cacaplaca.core.gps.LocationRepository
import com.prefeitura.cacaplaca.feature.home.ui.HomeViewModel
import com.prefeitura.cacaplaca.core.session.SessionManager
import com.prefeitura.cacaplaca.feature.auth.AuthViewModel
import com.prefeitura.cacaplaca.feature.auth.data.AuthRepository
import com.prefeitura.cacaplaca.feature.map.MapViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

import com.prefeitura.cacaplaca.BuildConfig
import com.prefeitura.cacaplaca.feature.auth.data.MockAuthRepository
import com.prefeitura.cacaplaca.feature.auth.data.RemoteAuthRepository

val appModule = module {
    singleOf(::LocationRepository)
    singleOf(::SessionManager)
    
    single<AuthRepository> {
        if (BuildConfig.IS_MOCK_AUTH) {
            MockAuthRepository(get(), get())
        } else {
            RemoteAuthRepository(get(), get())
        }
    }
    
    viewModelOf(::MapViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AuthViewModel)
}