package com.sgmobile.earthquake.feature.earthquake.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

internal object EarthquakeRoutes {

    @Serializable
    data object Overview : NavKey

    @Serializable
    data object Detail : NavKey
}
