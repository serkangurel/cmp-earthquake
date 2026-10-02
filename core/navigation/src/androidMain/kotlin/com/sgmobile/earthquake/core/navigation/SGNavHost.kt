package com.sgmobile.earthquake.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.plus
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.getKoin
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SGNavHost(
    modifier: Modifier = Modifier,
    startDestination: NavKey? = null,
    navigationComponents: List<NavigationComponent> = rememberNavigationComponents(),
) {
    val topLevelDestinations = remember(navigationComponents) {
        navigationComponents
            .mapNotNull { it.topLevelDestination }
            .sortedBy { it.order }
    }
    val startRoute = startDestination
        ?: topLevelDestinations.firstOrNull()?.route
        ?: error("No start destination found in navigation components.")

    val savedStateConfiguration = remember(navigationComponents) {
        SavedStateConfiguration {
            serializersModule = navigationComponents
                .map(NavigationComponent::serializersModule)
                .reduce { acc, module -> acc + module }
        }
    }
    val navigationState = rememberNavigationState(
        startRoute = startRoute,
        topLevelRoutes = topLevelDestinations.map { it.route },
        configuration = savedStateConfiguration,
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }
    val entryProvider = koinEntryProvider<NavKey>()
    val entries = navigationState.toEntries(entryProvider)
    val topLevelContentKeys = topLevelDestinations
        .map { entryProvider(it.route).contentKey }
        .toSet()
    val layoutDirection = LocalLayoutDirection.current
    val shouldShowBottomBar = navigationComponents.all { component ->
        component.showBottomBarEvaluator(navigationState.currentKey)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AnimatedBottomBar(isVisible = shouldShowBottomBar) {
                BottomNavigationBar(
                    items = topLevelDestinations.map { destination ->
                        BottomBarItem(
                            label = stringResource(destination.labelStringResource),
                            selectedIcon = destination.selectedIcon,
                            unselectedIcon = destination.unselectedIcon,
                        )
                    },
                    selectedIndex = topLevelDestinations.indexOfFirst {
                        it.route == navigationState.topLevelRoute
                    },
                    onItemClick = { navigator.navigate(topLevelDestinations[it].route) },
                )
            }
        },
    ) { paddingValues ->
        CompositionLocalProvider(
            LocalNavigator provides navigator,
            LocalNavScaffoldPadding provides paddingValues,
        ) {
            NavDisplay(
                entries = entries,
                modifier = modifier.fillMaxSize(),
                onBack = { navigator.goBack() },
                transitionSpec = {
                    navigationTransition(topLevelContentKeys, layoutDirection, isPop = false)
                },
                popTransitionSpec = {
                    navigationTransition(topLevelContentKeys, layoutDirection, isPop = true)
                },
                predictivePopTransitionSpec = {
                    navigationTransition(topLevelContentKeys, layoutDirection, isPop = true)
                },
            )
        }
    }
}

@Composable
private fun rememberNavigationComponents(
    providers: List<NavigationProvider> = getKoin().getAll<NavigationProvider>(),
): List<NavigationComponent> = remember(providers) {
    providers.map { it() }
}
