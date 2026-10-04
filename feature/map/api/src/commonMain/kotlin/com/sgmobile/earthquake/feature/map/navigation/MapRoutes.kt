package com.sgmobile.earthquake.feature.map.navigation

import com.sgmobile.earthquake.core.navigation.NavigationDestination

object MapRoutes {
    data object Overview : NavigationDestination {
        override val key: String = "map/overview"
        override val showsBottomBar: Boolean = true
    }
}
