package com.sgmobile.earthquake.feature.settings.navigation

import com.sgmobile.earthquake.feature.settings.presentation.SettingsScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val settingsNavigationModule: Module = module {
    navigation<SettingsRoutes.Overview> {
        SettingsScreen()
    }
}
