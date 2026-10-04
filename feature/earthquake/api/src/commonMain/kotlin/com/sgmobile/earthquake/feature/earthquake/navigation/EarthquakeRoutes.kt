package com.sgmobile.earthquake.feature.earthquake.navigation

import com.sgmobile.earthquake.core.navigation.NavigationDestination

object EarthquakeRoutes {
    data object Overview : NavigationDestination {
        override val key: String = "earthquake/overview"
        override val showsBottomBar: Boolean = true
    }

    data class Detail(val id: String) : NavigationDestination {
        override val key: String
            get() = "earthquake/detail/$id"
        override val showsBottomBar: Boolean = false

        companion object {
            fun fromKey(key: String): Detail? =
                key.takeIf { it.startsWith("earthquake/detail/") }
                    ?.removePrefix("earthquake/detail/")?.let(::Detail)
        }
    }
}
