package com.sgmobile.earthquake.feature.earthquake.navigation

import com.sgmobile.earthquake.feature.earthquake.detail.presentation.EarthquakeDetailScreen
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.EarthquakeScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val earthquakeNavigationModule: Module = module {
    navigation<EarthquakeRoutes.Overview> {
        EarthquakeScreen()
    }
    navigation<EarthquakeRoutes.Detail> {
        EarthquakeDetailScreen()
    }
}
