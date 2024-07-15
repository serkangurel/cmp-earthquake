package di

import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module
import ui.screens.earthquake.EarthquakeViewModel
import ui.screens.map.MapViewModel
import ui.screens.settings.SettingsViewModel

fun viewModelModule(): Module = module {
    viewModelOf(::EarthquakeViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::SettingsViewModel)
}