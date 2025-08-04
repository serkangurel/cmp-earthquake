package com.sgmobile.earthquake.extension

import androidx.navigation.NavDestination
import com.sgmobile.earthquake.Screens
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

@OptIn(InternalSerializationApi::class)
fun NavDestination.isRouteMatching(screen: Screens): Boolean {
    return route == screen::class.serializer().descriptor.serialName
}