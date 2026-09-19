package com.sgmobile.earthquake.feature.earthquake.navigation

import com.sgmobile.earthquake.core.navigation.navigationWithContentKey
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.EarthquakeDetailScreen
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.EarthquakeScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val earthquakeNavigationModule: Module = module {
    navigationWithContentKey<EarthquakeRoutes.Overview>(
        contentKey = { "earthquake/overview" },
    ) {
        EarthquakeScreen()
    }
    navigationWithContentKey<EarthquakeRoutes.Detail>(
        contentKey = { route -> "earthquake/detail/${route.id}" },
    ) { route ->
        EarthquakeDetailScreen(earthquakeId = route.id)
    }
}
