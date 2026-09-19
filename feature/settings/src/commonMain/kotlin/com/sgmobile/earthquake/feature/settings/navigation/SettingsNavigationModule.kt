package com.sgmobile.earthquake.feature.settings.navigation

import com.sgmobile.earthquake.core.navigation.navigationWithContentKey
import com.sgmobile.earthquake.feature.settings.presentation.SettingsScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val settingsNavigationModule: Module = module {
    navigationWithContentKey<SettingsRoutes.Overview>(
        contentKey = { "settings/overview" },
    ) {
        SettingsScreen()
    }
}
