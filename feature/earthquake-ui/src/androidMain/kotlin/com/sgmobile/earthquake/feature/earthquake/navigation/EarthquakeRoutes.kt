package com.sgmobile.earthquake.feature.earthquake.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

fun earthquakeDetailRoute(id: String): NavKey = EarthquakeRoutes.Detail(id)

internal object EarthquakeRoutes {

    @Serializable
    data object Overview : NavKey

    @Serializable
    data class Detail(val id: String) : NavKey
}
