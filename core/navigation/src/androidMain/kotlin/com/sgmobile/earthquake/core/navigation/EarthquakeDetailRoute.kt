package com.sgmobile.earthquake.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Route to the earthquake detail screen. Lives here so any tab can open it without depending on
 * the earthquake feature, which registers its destination.
 */
@Serializable
data class EarthquakeDetailRoute(val id: String) : NavKey
