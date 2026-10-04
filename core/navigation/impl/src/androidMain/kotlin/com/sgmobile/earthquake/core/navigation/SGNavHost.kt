package com.sgmobile.earthquake.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.NavDisplay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.getKoin
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SGNavHost(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    navigationComponents: List<NavigationComponent> = rememberNavigationComponents(),
) {
    val navigationState by navigator.state.collectAsStateWithLifecycle()
    val rootStyles = remember(navigationComponents) {
        navigationComponents.mapNotNull { it.topLevelDestination }.associateBy { it.route.key }
    }
    val topLevelDestinations = navigationState.backStacks.map { stack ->
        rootStyles[stack.root.key] ?: error("No Android destination registered for ${stack.root.key}")
    }
    val entryProvider = koinEntryProvider<NavigationDestination>()
    val entries = navigationState.toEntries(entryProvider)
    val topLevelContentKeys = topLevelDestinations
        .map { scopedContentKey(it.route, entryProvider(it.route).contentKey) }
        .toSet()
    val layoutDirection = LocalLayoutDirection.current
    val shouldShowBottomBar = navigationState.showsBottomBar

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
                        it.route.key == navigationState.selectedRoot.key
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
