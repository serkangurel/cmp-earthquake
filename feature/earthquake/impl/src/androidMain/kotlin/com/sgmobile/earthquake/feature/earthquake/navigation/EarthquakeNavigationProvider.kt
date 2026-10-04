package com.sgmobile.earthquake.feature.earthquake.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.outlined.MonitorHeart
import com.sgmobile.earthquake.core.navigation.NavigationComponent
import com.sgmobile.earthquake.core.navigation.NavigationProvider
import com.sgmobile.earthquake.core.navigation.TopLevelDestination
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquakes

internal class EarthquakeNavigationProvider : NavigationProvider {
    override fun invoke() =
        NavigationComponent(
            topLevelDestination = TopLevelDestination(
                selectedIcon = Icons.Filled.MonitorHeart,
                unselectedIcon = Icons.Outlined.MonitorHeart,
                labelStringResource = Res.string.earthquakes,
                route = EarthquakeRoutes.Overview,
            ),
        )
}
