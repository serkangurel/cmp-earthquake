package com.sgmobile.earthquake.core.navigation

/** Stable identity and presentation metadata shared by native navigation adapters. */
interface NavigationDestination {
    val key: String
    val showsBottomBar: Boolean
}
