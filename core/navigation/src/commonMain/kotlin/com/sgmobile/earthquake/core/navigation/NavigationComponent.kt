package com.sgmobile.earthquake.core.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

data class NavigationComponent(
    val navigationGraphBuilder: NavGraphBuilder.() -> Unit,
    val showBottomBarEvaluator: (NavBackStackEntry) -> Boolean = { true },
    val topLevelDestination: TopLevelDestination? = null,
)

data class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelStringResource: StringResource,
    val route: Any,
    val baseRoute: KClass<*> = route::class,
    val order: Int = 0,
)