package com.sgmobile.earthquake.core.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.compose.resources.StringResource

data class NavigationComponent(
    val serializersModule: SerializersModule,
    val showBottomBarEvaluator: (NavKey) -> Boolean = { true },
    val topLevelDestination: TopLevelDestination? = null,
)

data class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelStringResource: StringResource,
    val route: NavKey,
    val order: Int = 0,
)