package com.sgmobile.earthquake.feature.map.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Map
import com.sgmobile.earthquake.core.navigation.NavigationComponent
import com.sgmobile.earthquake.core.navigation.NavigationProvider
import com.sgmobile.earthquake.core.navigation.TopLevelDestination
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.map

internal class MapNavigationProvider : NavigationProvider {
    override fun invoke() =
        NavigationComponent(
            topLevelDestination = TopLevelDestination(
                selectedIcon = Icons.Filled.Map,
                unselectedIcon = Icons.Outlined.Map,
                labelStringResource = Res.string.map,
                route = MapRoutes.Overview,
            ),
        )
}
