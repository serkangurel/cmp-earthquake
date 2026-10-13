package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeSource
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold

sealed interface EarthquakeScreenIntent {
    data class Refresh(val isPullToRefresh: Boolean) : EarthquakeScreenIntent
    data object LoadMore : EarthquakeScreenIntent
    data object Retry : EarthquakeScreenIntent
    data class SelectMagnitude(val selectedMagnitude: MagnitudeThreshold) : EarthquakeScreenIntent
    data class SelectCountry(val country: CountryOption) : EarthquakeScreenIntent
    data class SelectSource(val source: EarthquakeSource) : EarthquakeScreenIntent
}
