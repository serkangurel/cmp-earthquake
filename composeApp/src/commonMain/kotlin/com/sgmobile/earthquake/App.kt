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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sgmobile.earthquake.di.getKoinConfiguration
import com.sgmobile.earthquake.ui.component.SGAppBar
import com.sgmobile.earthquake.ui.screens.earthquake.EarthquakeScreen
import com.sgmobile.earthquake.ui.screens.map.MapScreen
import com.sgmobile.earthquake.ui.screens.settings.SettingsScreen
import com.sgmobile.earthquake.ui.theme.AppTheme
import com.sgmobile.earthquake.util.SetSystemBarsLightAppearance
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.aakira.napier.log
import kotlinx.serialization.Serializable
import org.koin.compose.KoinMultiplatformApplication

@Serializable
sealed class Screens(val label: String)

@Serializable
data object Earthquakes : Screens(label = "Earthquakes")

@Serializable
data object Map : Screens(label = "Map")

@Serializable
data object Settings : Screens(label = "Settings")

data class BottomNavigationItem(
    val screen: Screens,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

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
                isAppearanceLightStatusBars = !darkTheme,
                isAppearanceLightNavigationBars = !darkTheme
            )
            var selectedItemIndex by rememberSaveable {
                mutableStateOf(0)
            }

            var screenTitle by rememberSaveable {
                mutableStateOf(Earthquakes.label)
            }

            val items = listOf(
                BottomNavigationItem(
                    screen = Earthquakes,
                    label = Earthquakes.label,
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home
                ),
                BottomNavigationItem(
                    screen = Map,
                    label = Map.label,
                    selectedIcon = Icons.Filled.Map,
                    unselectedIcon = Icons.Outlined.Map
                ),
                BottomNavigationItem(
                    screen = Settings,
                    label = Settings.label,
                    selectedIcon = Icons.Filled.Settings,
                    unselectedIcon = Icons.Outlined.Settings
                ),
            )

            Scaffold(
                topBar = {
                    SGAppBar(
                        screenTitle = screenTitle,
                        canNavigateBack = false,
                        navigateUp = { navController.navigateUp() }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selectedItemIndex == index,
                                onClick = {
                                    selectedItemIndex = index
                                    screenTitle = item.label
                                    navController.navigate(item.screen) {
                                        navController.graph.findStartDestination().route?.let { route ->
                                            popUpTo(route) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                label = {
                                    Text(text = item.label)
                                },
                                alwaysShowLabel = true,
                                icon = {
                                    Icon(
                                        imageVector = if (index == selectedItemIndex) {
                                            item.selectedIcon
                                        } else
                                            item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                }
                            )
                        }
                    }
                },
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Earthquakes,
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
                    composable<Earthquakes> {
                        EarthquakeScreen()
                    }

                    composable<Map> {
                        MapScreen()
                    }

                    composable<Settings> {
                        SettingsScreen()
                    }
                }
            }
        }
    }
}

fun appInit() {
    Napier.base(DebugAntilog())
    log(tag = "Napier") { "Application Started" }
}