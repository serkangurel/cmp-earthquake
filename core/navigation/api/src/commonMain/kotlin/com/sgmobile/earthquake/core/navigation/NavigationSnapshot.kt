package com.sgmobile.earthquake.core.navigation

data class NavigationBackStack(
    val root: NavigationDestination,
    val destinations: List<NavigationDestination>,
)

data class NavigationSnapshot(
    val startRoute: NavigationDestination,
    val selectedRoot: NavigationDestination,
    val backStacks: List<NavigationBackStack>,
) {
    val currentDestination: NavigationDestination
        get() = backStacks.first { it.root.key == selectedRoot.key }.destinations.last()

    val showsBottomBar: Boolean
        get() = currentDestination.showsBottomBar
}
