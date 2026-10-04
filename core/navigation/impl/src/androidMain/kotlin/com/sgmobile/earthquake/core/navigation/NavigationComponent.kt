package com.sgmobile.earthquake.core.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

data class NavigationComponent(
    val topLevelDestination: TopLevelDestination? = null,
)

data class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelStringResource: StringResource,
    val route: NavigationDestination,
)
