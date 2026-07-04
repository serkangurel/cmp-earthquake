package com.sgmobile.earthquake.feature.settings.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.navigation3.runtime.NavKey
import com.sgmobile.earthquake.core.navigation.NavigationComponent
import com.sgmobile.earthquake.core.navigation.NavigationProvider
import com.sgmobile.earthquake.core.navigation.TopLevelDestination
import com.sgmobile.earthquake.core.resource.Res
import com.sgmobile.earthquake.core.resource.settings
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.core.annotation.Single

@Single(binds = [NavigationProvider::class])
internal class SettingsNavigationProvider : NavigationProvider {
    override fun invoke() =
        NavigationComponent(
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(SettingsRoutes.Overview::class, SettingsRoutes.Overview.serializer())
                }
            },
            topLevelDestination = TopLevelDestination(
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Filled.Settings,
                labelStringResource = Res.string.settings,
                route = SettingsRoutes.Overview,
                order = 3
            ),
        )
}
