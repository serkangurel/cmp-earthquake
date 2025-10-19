package com.sgmobile.earthquake.feature.map.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.navigation.NavGraphBuilder
import com.sgmobile.earthquake.core.navigation.NavigationComponent
import com.sgmobile.earthquake.core.navigation.NavigationProvider
import com.sgmobile.earthquake.core.navigation.TopLevelDestination
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.map
import org.koin.core.annotation.Single

@Single(binds = [NavigationProvider::class])
internal class MapNavigationProvider : NavigationProvider {
    override fun invoke() =
        NavigationComponent(
            navigationGraphBuilder = NavGraphBuilder::mapNavigationGraph,
            topLevelDestination = TopLevelDestination(
                selectedIcon = Icons.Filled.Map,
                unselectedIcon = Icons.Filled.Map,
                labelStringResource = Res.string.map,
                route = MapRoutes.Overview,
                order = 2
            ),
        )
}