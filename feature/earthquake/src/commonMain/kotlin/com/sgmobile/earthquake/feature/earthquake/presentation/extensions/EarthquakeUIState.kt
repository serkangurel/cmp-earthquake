package com.sgmobile.earthquake.feature.earthquake.presentation.extensions

import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.presentation.EarthquakeUIState
import com.sgmobile.earthquake.feature.earthquake.presentation.models.EarthquakeVo

internal fun List<Earthquake>.mapToUi(): EarthquakeUIState {
    val earhtquakeList = this.map { item ->
        EarthquakeVo(
            place = item.place,
            magnitude = item.magnitude,
            magnitudeThreshold = item.magnitudeThreshold,
            date = item.date
        )
    }

    return EarthquakeUIState(
        isLoading = false,
        isPullToRefresh = false,
        isEndReached = false,
        earhtquakeList = earhtquakeList,
        selectedMagnitude = MagnitudeThreshold.TWO_PLUS
    )
}
