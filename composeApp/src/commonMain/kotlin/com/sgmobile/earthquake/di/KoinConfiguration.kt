package com.sgmobile.earthquake.di

import com.sgmobile.earthquake.core.navigation.di.NavigationModule
import com.sgmobile.earthquake.core.network.di.NetworkModule
import com.sgmobile.earthquake.feature.earthquake.di.EarthquakeModule
import com.sgmobile.earthquake.feature.map.di.MapModule
import com.sgmobile.earthquake.feature.settings.di.SettingsModule
import org.koin.core.annotation.Module
import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.module

@Module(
    includes = [
        NavigationModule::class,
        NetworkModule::class,
        EarthquakeModule::class,
        MapModule::class,
        SettingsModule::class,
    ]
)
class AllModules

fun getKoinConfiguration(): KoinConfiguration = KoinConfiguration {
    modules(
        AllModules().module,
    )
}