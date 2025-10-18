package com.sgmobile.earthquake.feature.settings.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.navigation.NavGraphBuilder
import com.sgmobile.earthquake.core.navigation.NavigationComponent
import com.sgmobile.earthquake.core.navigation.NavigationProvider
import com.sgmobile.earthquake.core.navigation.TopLevelDestination
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.settings
import org.koin.core.annotation.Single

@Single(binds = [NavigationProvider::class])
internal class SettingsNavigationProvider : NavigationProvider {
    override fun invoke() =
        NavigationComponent(
            navigationGraphBuilder = NavGraphBuilder::settingsNavigationGraph,
            topLevelDestination = TopLevelDestination(
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Filled.Settings,
                labelStringResource = Res.string.settings,
                route = SettingsRoutes.Overview,
                order = 3
            ),
        )
}