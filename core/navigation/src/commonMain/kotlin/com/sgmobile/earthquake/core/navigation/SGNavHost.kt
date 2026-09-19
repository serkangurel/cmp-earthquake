package com.sgmobile.earthquake.core.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.mohamedrejeb.calf.ui.ExperimentalCalfUiApi
import com.mohamedrejeb.calf.ui.navigation.AdaptiveNavigationBar
import com.mohamedrejeb.calf.ui.navigation.AdaptiveScaffold
import com.mohamedrejeb.calf.ui.navigation.UIKitUITabBarItem
import com.mohamedrejeb.calf.ui.uikit.UIKitImage
import kotlinx.serialization.modules.plus
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.getKoin
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class, ExperimentalCalfUiApi::class)
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
    val entries = navigationState.toEntries(koinEntryProvider())

    val shouldShowBottomBar = navigationComponents.all { component ->
        component.showBottomBarEvaluator(navigationState.currentKey)
    }

    AdaptiveScaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            BottomNavigationBar(
                isVisible = shouldShowBottomBar,
                destinations = topLevelDestinations,
                selectedRoute = navigationState.topLevelRoute,
                onDestinationClick = { navigator.navigate(it.route) },
            )
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
                    EnterTransition.None togetherWith ExitTransition.None
                },
                popTransitionSpec = {
                    EnterTransition.None togetherWith ExitTransition.None
                },
                predictivePopTransitionSpec = {
                    EnterTransition.None togetherWith ExitTransition.None
                },
            )
        }
    }
}

@OptIn(ExperimentalCalfUiApi::class)
@Composable
private fun BottomNavigationBar(
    isVisible: Boolean,
    destinations: List<TopLevelDestination>,
    selectedRoute: NavKey,
    onDestinationClick: (TopLevelDestination) -> Unit,
) {
    AnimatedBottomBar(isVisible = isVisible) {
        val selectedIndex = destinations
            .indexOfFirst { it.route == selectedRoute }
            .coerceAtLeast(0)
        val iosItems = destinations.map { destination ->
            UIKitUITabBarItem(
                title = stringResource(destination.labelStringResource),
                image = UIKitImage.Vector(destination.unselectedIcon),
                selectedImage = UIKitImage.Vector(destination.selectedIcon),
            )
        }

        AdaptiveNavigationBar(
            iosItems = iosItems,
            iosSelectedIndex = selectedIndex,
            iosOnItemSelected = { index ->
                destinations.getOrNull(index)?.let(onDestinationClick)
            },
        ) {
            destinations.forEach { destination ->
                BottomNavigationItem(
                    destination = destination,
                    isSelected = destination.route == selectedRoute,
                    onClick = { onDestinationClick(destination) },
                )
            }
        }
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
                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                contentDescription = null,
            )
        },
        label = {
            Text(text = stringResource(destination.labelStringResource))
        },
    )
}

@Composable
private fun rememberNavigationComponents(
    providers: List<NavigationProvider> = getKoin().getAll<NavigationProvider>(),
): List<NavigationComponent> =
    remember {
        providers.map { it() }
    }
