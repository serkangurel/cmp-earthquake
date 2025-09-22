package com.sgmobile.earthquake.di

import com.sgmobile.earthquake.data.EarthquakeRepository
import com.sgmobile.earthquake.ui.screens.earthquake.EarthquakeViewModel
import com.sgmobile.earthquake.ui.screens.map.MapViewModel
import com.sgmobile.earthquake.ui.screens.settings.SettingsViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.koin.core.annotation.Provided

@Module
class ViewModelModule {
    @KoinViewModel
    fun provideEarthquakeViewModel(
        @Provided
        earthquakeRepository: EarthquakeRepository
    ) = EarthquakeViewModel(earthquakeRepository)

    @KoinViewModel
    fun provideMapViewModel() = MapViewModel()

    @KoinViewModel
    fun provideSettingsViewModel() = SettingsViewModel()
}