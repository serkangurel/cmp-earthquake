package com.sgmobile.earthquake.feature.earthquake.presentation

internal sealed interface EarthquakeScreenIntent {
    data object Refresh : EarthquakeScreenIntent
    data object LoadMore : EarthquakeScreenIntent
}