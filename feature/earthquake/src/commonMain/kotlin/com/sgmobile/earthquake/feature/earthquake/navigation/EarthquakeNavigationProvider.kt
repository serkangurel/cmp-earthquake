package com.sgmobile.earthquake.feature.earthquake.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.navigation.NavGraphBuilder
import com.sgmobile.earthquake.core.navigation.NavigationComponent
import com.sgmobile.earthquake.core.navigation.NavigationProvider
import com.sgmobile.earthquake.core.navigation.TopLevelDestination
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquakes
import org.koin.core.annotation.Single

@Single(binds = [NavigationProvider::class])
internal class EarthquakeNavigationProvider : NavigationProvider {
    override fun invoke() =
        NavigationComponent(
            navigationGraphBuilder = NavGraphBuilder::earthquakeNavigationGraph,
            topLevelDestination = TopLevelDestination(
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Filled.Home,
                labelStringResource = Res.string.earthquakes,
                route = EarthquakeRoutes.Overview,
                order = 1
            ),
        )
}
