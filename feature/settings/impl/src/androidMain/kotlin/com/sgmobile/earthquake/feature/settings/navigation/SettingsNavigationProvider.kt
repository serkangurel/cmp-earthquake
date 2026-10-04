package com.sgmobile.earthquake.feature.settings.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import com.sgmobile.earthquake.core.navigation.NavigationComponent
import com.sgmobile.earthquake.core.navigation.NavigationProvider
import com.sgmobile.earthquake.core.navigation.TopLevelDestination
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.settings

internal class SettingsNavigationProvider : NavigationProvider {
    override fun invoke() =
        NavigationComponent(
            topLevelDestination = TopLevelDestination(
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                labelStringResource = Res.string.settings,
                route = SettingsRoutes.Overview,
            ),
        )
}
