package com.sgmobile.earthquake.feature.map.navigation

import com.sgmobile.earthquake.feature.map.presentation.MapScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val mapNavigationModule: Module = module {
    navigation<MapRoutes.Overview> {
        MapScreen()
    }
}
