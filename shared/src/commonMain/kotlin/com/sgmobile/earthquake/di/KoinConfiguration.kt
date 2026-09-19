package com.sgmobile.earthquake.di

import com.sgmobile.earthquake.core.navigation.di.NavigationModule
import com.sgmobile.earthquake.core.network.di.NetworkModule
import com.sgmobile.earthquake.feature.earthquake.di.EarthquakeModule
import com.sgmobile.earthquake.feature.earthquake.navigation.earthquakeNavigationModule
import com.sgmobile.earthquake.feature.map.di.MapModule
import com.sgmobile.earthquake.feature.map.navigation.mapNavigationModule
import com.sgmobile.earthquake.feature.settings.di.SettingsModule
import com.sgmobile.earthquake.feature.settings.navigation.settingsNavigationModule
import org.koin.core.annotation.KoinApplication
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.includes
import org.koin.dsl.koinConfiguration
import org.koin.plugin.module.dsl.koinConfiguration as annotatedKoinConfiguration

@KoinApplication(
    modules = [
        NavigationModule::class,
        NetworkModule::class,
        EarthquakeModule::class,
        MapModule::class,
        SettingsModule::class,
    ]
)
class MyApp

fun getKoinConfiguration(): KoinConfiguration = koinConfiguration {
    includes(annotatedKoinConfiguration<MyApp>())
    modules(
        earthquakeNavigationModule,
        mapNavigationModule,
        settingsNavigationModule,
    )
}
