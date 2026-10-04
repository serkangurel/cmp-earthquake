package com.sgmobile.earthquake.feature.settings.navigation

import com.sgmobile.earthquake.core.navigation.NavigationDestination

object SettingsRoutes {
    data object Overview : NavigationDestination {
        override val key: String = "settings/overview"
        override val showsBottomBar: Boolean = true
    }

    data object Appearance : NavigationDestination {
        override val key: String = "settings/appearance"
        override val showsBottomBar: Boolean = false
    }

    data object DefaultFilters : NavigationDestination {
        override val key: String = "settings/default-filters"
        override val showsBottomBar: Boolean = false
    }

    data object DefaultCountry : NavigationDestination {
        override val key: String = "settings/default-country"
        override val showsBottomBar: Boolean = false
    }

    data object TimeRange : NavigationDestination {
        override val key: String = "settings/time-range"
        override val showsBottomBar: Boolean = false
    }

    data object About : NavigationDestination {
        override val key: String = "settings/about"
        override val showsBottomBar: Boolean = false
    }

    private val subScreens: List<NavigationDestination> =
        listOf(Appearance, DefaultFilters, DefaultCountry, TimeRange, About)

    /** Resolves pushed settings screens; the tab root is resolved with the other roots. */
    fun fromKey(key: String): NavigationDestination? = subScreens.firstOrNull { it.key == key }
}
