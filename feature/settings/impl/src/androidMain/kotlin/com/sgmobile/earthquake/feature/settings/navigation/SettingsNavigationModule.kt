package com.sgmobile.earthquake.feature.settings.navigation

import com.sgmobile.earthquake.core.navigation.NavigationProvider
import com.sgmobile.earthquake.core.navigation.navigationWithContentKey
import com.sgmobile.earthquake.feature.settings.presentation.AboutScreen
import com.sgmobile.earthquake.feature.settings.presentation.AppearanceScreen
import com.sgmobile.earthquake.feature.settings.presentation.DefaultCountryScreen
import com.sgmobile.earthquake.feature.settings.presentation.DefaultFiltersScreen
import com.sgmobile.earthquake.feature.settings.presentation.SettingsScreen
import com.sgmobile.earthquake.feature.settings.presentation.TimeRangeScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val settingsNavigationModule: Module = module {
    single { SettingsNavigationProvider() } bind NavigationProvider::class
    navigationWithContentKey<SettingsRoutes.Overview>(
        contentKey = { "settings/overview" },
    ) {
        SettingsScreen()
    }
    navigationWithContentKey<SettingsRoutes.Appearance>(
        contentKey = { "settings/appearance" },
    ) {
        AppearanceScreen()
    }
    navigationWithContentKey<SettingsRoutes.DefaultFilters>(
        contentKey = { "settings/default-filters" },
    ) {
        DefaultFiltersScreen()
    }
    navigationWithContentKey<SettingsRoutes.DefaultCountry>(
        contentKey = { "settings/default-country" },
    ) {
        DefaultCountryScreen()
    }
    navigationWithContentKey<SettingsRoutes.TimeRange>(
        contentKey = { "settings/time-range" },
    ) {
        TimeRangeScreen()
    }
    navigationWithContentKey<SettingsRoutes.About>(
        contentKey = { "settings/about" },
    ) {
        AboutScreen()
    }
}
