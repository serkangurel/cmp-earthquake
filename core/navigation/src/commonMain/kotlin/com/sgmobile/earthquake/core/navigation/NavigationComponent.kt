package com.sgmobile.earthquake.core.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

data class NavigationComponent(
    val navigationGraphBuilder: NavGraphBuilder.() -> Unit,
    val showBottomBarEvaluator: (NavBackStackEntry) -> Boolean = { true },
    val topLevelDestination: TopLevelDestination? = null,
)

data class TopLevelDestination(
    val selectedIcon: DrawableResource,
    val unselectedIcon: DrawableResource,
    val labelStringResource: StringResource,
    val route: Any,
    val baseRoute: KClass<*> = route::class,
    val order: Int = 0,
)
