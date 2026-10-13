package com.prefeitura.cacaplaca.di

import com.prefeitura.cacaplaca.core.gps.LocationRepository
import com.prefeitura.cacaplaca.feature.home.ui.HomeViewModel
import com.prefeitura.cacaplaca.feature.map.MapViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::LocationRepository)
    viewModelOf(::MapViewModel)
    viewModelOf(::HomeViewModel)
}