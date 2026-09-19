package com.sgmobile.earthquake.feature.earthquake.overview.presentation

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeVo

internal data class EarthquakeUIState(
    val isLoading: Boolean,
    val isPullToRefresh: Boolean,
    val isEndReached: Boolean,
    val earhtquakeList: List<EarthquakeVo>,
    val selectedMagnitude: MagnitudeThreshold
) {
    companion object Companion {
        val INITIAL = EarthquakeUIState(
            isLoading = false,
            isPullToRefresh = false,
            isEndReached = false,
            earhtquakeList = listOf(),
            selectedMagnitude = MagnitudeThreshold.TWO_PLUS
        )
    }
}