package com.sgmobile.earthquake.feature.settings.navigation

import com.sgmobile.earthquake.core.navigation.NavigationDestination

object SettingsRoutes {
    data object Overview : NavigationDestination {
        override val key: String = "settings/overview"
        override val showsBottomBar: Boolean = true
    }
}
