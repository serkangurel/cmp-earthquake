package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold

internal sealed interface EarthquakeScreenIntent {
    data class Refresh(val isPullToRefresh: Boolean) : EarthquakeScreenIntent
    data object LoadMore : EarthquakeScreenIntent
    data class SelectMagnitude(val selectedMagnitude: MagnitudeThreshold) : EarthquakeScreenIntent
}