package com.sgmobile.earthquake.feature.earthquake.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sgmobile.earthquake.feature.earthquake.presentation.EarthquakeScreen

internal fun NavGraphBuilder.earthquakeNavigationGraph() {
    composable<EarthquakeRoutes.Overview> {
        EarthquakeScreen()
    }
}
