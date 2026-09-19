package com.sgmobile.earthquake.feature.map.navigation

import com.sgmobile.earthquake.core.navigation.navigationWithContentKey
import com.sgmobile.earthquake.feature.map.presentation.MapScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val mapNavigationModule: Module = module {
    navigationWithContentKey<MapRoutes.Overview>(
        contentKey = { "map/overview" },
    ) {
        MapScreen()
    }
}
