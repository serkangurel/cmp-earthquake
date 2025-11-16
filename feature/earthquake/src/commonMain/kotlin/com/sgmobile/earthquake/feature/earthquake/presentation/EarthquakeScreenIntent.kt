package com.sgmobile.earthquake.feature.earthquake.presentation

import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold

internal sealed interface EarthquakeScreenIntent {
    data class Refresh(val isPullToRefresh: Boolean) : EarthquakeScreenIntent
    data object LoadMore : EarthquakeScreenIntent
    data class SelectMagnitude(val selectedMagnitude: MagnitudeThreshold) : EarthquakeScreenIntent
}