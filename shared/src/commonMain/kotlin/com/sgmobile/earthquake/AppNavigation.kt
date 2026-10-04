package com.sgmobile.earthquake

import com.sgmobile.earthquake.core.navigation.Navigator
import com.sgmobile.earthquake.core.navigation.createNavigator
import com.sgmobile.earthquake.feature.earthquake.navigation.EarthquakeRoutes
import com.sgmobile.earthquake.feature.map.navigation.MapRoutes
import com.sgmobile.earthquake.feature.settings.navigation.SettingsRoutes

fun createAppNavigator(): Navigator {
    val roots = listOf(EarthquakeRoutes.Overview, MapRoutes.Overview, SettingsRoutes.Overview)
    return createNavigator(
        startRoute = EarthquakeRoutes.Overview,
        topLevelRoutes = roots,
        resolveDestination = { key ->
            roots.firstOrNull { it.key == key }
                ?: EarthquakeRoutes.Detail.fromKey(key)
                ?: SettingsRoutes.fromKey(key)
        },
    )
}
