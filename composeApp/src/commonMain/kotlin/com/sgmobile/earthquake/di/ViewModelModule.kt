package com.sgmobile.earthquake.di

import com.sgmobile.earthquake.ui.screens.earthquake.EarthquakeViewModel
import com.sgmobile.earthquake.ui.screens.map.MapViewModel
import com.sgmobile.earthquake.ui.screens.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun viewModelModule(): Module = module {
    viewModelOf(::EarthquakeViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::SettingsViewModel)
}