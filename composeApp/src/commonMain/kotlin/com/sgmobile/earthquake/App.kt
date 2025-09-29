package com.sgmobile.earthquake

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sgmobile.earthquake.core.ui.components.SGAppBar
import com.sgmobile.earthquake.core.ui.theme.AppTheme
import com.sgmobile.earthquake.di.getKoinConfiguration
import com.sgmobile.earthquake.extension.isRouteMatching
import com.sgmobile.earthquake.ui.screens.earthquake.EarthquakeScreen
import com.sgmobile.earthquake.ui.screens.map.MapScreen
import com.sgmobile.earthquake.ui.screens.settings.SettingsScreen
import com.sgmobile.earthquake.util.SetSystemBarsLightAppearance
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.aakira.napier.log
import kotlinx.serialization.Serializable
import org.koin.compose.KoinMultiplatformApplication
import org.koin.core.annotation.KoinExperimentalAPI

@Serializable
sealed class Screens(val label: String) {
    @Serializable
    data object Earthquakes : Screens("Earthquakes")

    @Serializable
    data object Map : Screens("Map")

    @Serializable
    data object Settings : Screens("Settings")
}

data class BottomNavigationItem(
    val screen: Screens,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    navController: NavHostController = rememberNavController()
) {
    KoinMultiplatformApplication(
        config = getKoinConfiguration()
    ) {
        AppTheme(
            darkTheme = darkTheme,
            dynamicColor = dynamicColor
        ) {
            SetSystemBarsLightAppearance(
                isAppearanceLightStatusBars = darkTheme,
                isAppearanceLightNavigationBars = !darkTheme
            )

            val bottomNavItems = remember {
                listOf(
                    BottomNavigationItem(
                        screen = Screens.Earthquakes,
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home
                    ),
                    BottomNavigationItem(
                        screen = Screens.Map,
                        selectedIcon = Icons.Filled.Map,
                        unselectedIcon = Icons.Outlined.Map
                    ),
                    BottomNavigationItem(
                        screen = Screens.Settings,
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings
                    )
                )
            }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val selectedItemIndex by rememberUpdatedState(
                bottomNavItems.indexOfFirst { item ->
                    currentDestination?.isRouteMatching(item.screen) ?: false
                }.coerceAtLeast(0)
            )
            Scaffold(
                topBar = {
                    SGAppBar(
                        screenTitle = bottomNavItems[selectedItemIndex].screen.label,
                        canNavigateBack = false,
                        navigateUp = { navController.navigateUp() }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        bottomNavItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selectedItemIndex == index,
                                onClick = {
                                    navController.navigate(item.screen) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                label = {
                                    Text(item.screen.label)
                                },
                                alwaysShowLabel = true,
                                icon = {
                                    Icon(
                                        imageVector = if (selectedItemIndex == index) {
                                            item.selectedIcon
                                        } else {
                                            item.unselectedIcon
                                        },
                                        contentDescription = item.screen.label
                                    )
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screens.Earthquakes,
                    enterTransition = {
                        EnterTransition.None
                    },
                    exitTransition = {
                        ExitTransition.None
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable<Screens.Earthquakes> { EarthquakeScreen() }
                    composable<Screens.Map> { MapScreen() }
                    composable<Screens.Settings> { SettingsScreen() }
                }
            }
        }
    }
}

fun appInit() {
    Napier.base(DebugAntilog())
    log(tag = "Napier") { "Application Started" }
}