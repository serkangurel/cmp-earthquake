package com.sgmobile.earthquake.feature.earthquake.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.navigation3.runtime.NavKey
import com.sgmobile.earthquake.core.navigation.NavigationComponent
import com.sgmobile.earthquake.core.navigation.NavigationProvider
import com.sgmobile.earthquake.core.navigation.TopLevelDestination
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.earthquakes
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.core.annotation.Single

@Single(binds = [NavigationProvider::class])
internal class EarthquakeNavigationProvider : NavigationProvider {
    override fun invoke() =
        NavigationComponent(
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(EarthquakeRoutes.Overview::class, EarthquakeRoutes.Overview.serializer())
                    subclass(EarthquakeRoutes.Detail::class, EarthquakeRoutes.Detail.serializer())
                }
            },
            topLevelDestination = TopLevelDestination(
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Filled.Home,
                labelStringResource = Res.string.earthquakes,
                route = EarthquakeRoutes.Overview,
                order = 1
            ),
            showBottomBarEvaluator = { navKey ->
                navKey !is EarthquakeRoutes.Detail
            },
        )
}
