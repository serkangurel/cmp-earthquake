package com.sgmobile.earthquake.feature.map.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sgmobile.earthquake.feature.map.presentation.MapScreen

internal fun NavGraphBuilder.mapNavigationGraph() {
    composable<MapRoutes.Overview> {
        MapScreen()
    }
}