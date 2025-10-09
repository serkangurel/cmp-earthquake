package com.sgmobile.earthquake.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sgmobile.earthquake.core.navigation.extension.isRouteInHierarchy
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.getKoin

@Composable
fun SGNavHost(
    modifier: Modifier = Modifier,
    startDestination: Any? = null,
    navController: NavHostController = rememberNavController(),
    navigationModules: List<NavigationModule> = rememberNavigationModules(),
) {
    val navState = rememberNavState(
        navigationModules = navigationModules,
        navController = navController,
    )
    val destinationResolver = rememberDestinationResolver(
        startDestination = startDestination,
        topLevelDestinations = navState.topLevelDestinations,
    )

    NavScaffold(
        navState = navState,
        navigationModules = navigationModules,
        navController = navController,
    ) { paddingValues ->
        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalNavScaffoldPadding provides paddingValues,
        ) {
            NavHost(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                startDestination = destinationResolver.resolvedStartDestination,
            ) {
                navigationModules.forEach { module ->
                    module.navigationGraphBuilder(this)
                }
            }
        }
    }
}

@Composable
private fun rememberNavState(
    navigationModules: List<NavigationModule>,
    navController: NavHostController,
): NavigationState {
    val topLevelDestinations = remember(navigationModules) {
        navigationModules
            .mapNotNull { it.topLevelDestination }
            .sortedBy { it.order }
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    return remember(topLevelDestinations, currentBackStackEntry) {
        NavigationState(
            topLevelDestinations = topLevelDestinations,
            currentDestination = currentBackStackEntry?.destination,
            currentBackStackEntry = currentBackStackEntry,
        )
    }
}

@Composable
private fun rememberDestinationResolver(
    startDestination: Any?,
    topLevelDestinations: List<TopLevelDestination>,
): DestinationResolver {
    return remember(startDestination, topLevelDestinations) {
        DestinationResolver(
            customStartDestination = startDestination,
            firstTopLevelDestination = topLevelDestinations.firstOrNull()?.route,
        )
    }
}

@Composable
private fun NavScaffold(
    navState: NavigationState,
    navigationModules: List<NavigationModule>,
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit,
) {
    val shouldShowBottomBar = remember(navState.currentDestination, navigationModules) {
        navigationModules.all { module ->
            module.showBottomBarEvaluator(navState.currentBackStackEntry ?: return@remember false)
        }
    }
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            BottomNavigationBar(
                isVisible = shouldShowBottomBar,
                destinations = navState.topLevelDestinations,
                currentDestination = navState.currentDestination,
                navController = navController,
            )
        },
        content = content,
    )
}

@Composable
private fun BottomNavigationBar(
    isVisible: Boolean,
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    navController: NavHostController,
) {
    AnimatedBottomBar(isVisible = isVisible) {
        NavigationBar {
            destinations.forEach { destination ->
                BottomNavigationItem(
                    destination = destination,
                    isSelected = currentDestination.isRouteInHierarchy(destination.baseRoute),
                    onClick = {
                        navController.navigate(destination.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun AnimatedBottomBar(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    animationDuration: Int = 300,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(animationDuration, easing = FastOutSlowInEasing),
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(animationDuration, easing = FastOutSlowInEasing),
        ),
        modifier = modifier,
    ) {
        content()
    }
}

@Composable
private fun RowScope.BottomNavigationItem(
    destination: TopLevelDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(
                    if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                ),
                contentDescription = null,
            )
        },
        label = {
            Text(text = stringResource(destination.labelStringResource))
        },
    )
}

private data class NavigationState(
    val topLevelDestinations: List<TopLevelDestination>,
    val currentDestination: NavDestination?,
    val currentBackStackEntry: NavBackStackEntry?,
)

private data class DestinationResolver(
    val customStartDestination: Any?,
    val firstTopLevelDestination: Any?,
) {
    val resolvedStartDestination: Any = customStartDestination
        ?: firstTopLevelDestination
        ?: error("No start destination found in navigation modules.")
}

@Composable
private fun rememberNavigationModules(
    providers: List<NavigationProvider> = getKoin().getAll<NavigationProvider>(),
): List<NavigationModule> =
    remember(providers) {
        providers.map { it() }
    }
