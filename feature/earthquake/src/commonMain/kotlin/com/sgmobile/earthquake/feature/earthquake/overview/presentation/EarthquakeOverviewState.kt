package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeListItem

data class EarthquakeOverviewState(
    val isLoading: Boolean,
    val isPullToRefresh: Boolean,
    val isEndReached: Boolean,
    val earthquakes: List<EarthquakeListItem>,
    val countries: List<CountryOption>,
    val selectedMagnitude: MagnitudeThreshold,
    val selectedCountry: CountryOption?,
) {
    companion object {
        val INITIAL = EarthquakeOverviewState(
            isLoading = false,
            isPullToRefresh = false,
            isEndReached = false,
            earthquakes = emptyList(),
            countries = emptyList(),
            selectedMagnitude = MagnitudeThreshold.TWO_PLUS,
            selectedCountry = null,
        )
    }
}
