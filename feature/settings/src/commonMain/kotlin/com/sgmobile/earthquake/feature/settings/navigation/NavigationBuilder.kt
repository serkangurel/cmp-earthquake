package com.sgmobile.earthquake.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sgmobile.earthquake.feature.settings.presentation.SettingsScreen

internal fun NavGraphBuilder.settingsNavigationGraph() {
    composable<SettingsRoutes.Overview> {
        SettingsScreen()
    }
}