package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.core.presentation.models.EarthquakeListItem

data class EarthquakeOverviewState(
    val isLoading: Boolean,
    val isPullToRefresh: Boolean,
    val isEndReached: Boolean,
    val earthquakes: List<EarthquakeListItem>,
    val countries: List<CountryOption>,
    val selectedMagnitude: MagnitudeThreshold,
    val selectedCountry: CountryOption?,
) {
    val showsBlockingLoader: Boolean
        get() = isLoading && earthquakes.isEmpty()

    val showsEmptyState: Boolean
        get() = earthquakes.isEmpty() && !isLoading && !isPullToRefresh

    val showsPagingLoader: Boolean
        get() = isLoading && earthquakes.isNotEmpty()

    // Platforms add their own layout conditions, such as accessibility text sizes.
    val canCollapseFilters: Boolean
        get() = earthquakes.isNotEmpty() && !isPullToRefresh

    val magnitudeOptions: List<MagnitudeThreshold>
        get() = MagnitudeThreshold.entries

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
