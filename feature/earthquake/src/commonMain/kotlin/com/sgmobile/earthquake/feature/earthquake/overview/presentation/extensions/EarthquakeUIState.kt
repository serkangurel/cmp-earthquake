package com.sgmobile.earthquake.feature.earthquake.overview.presentation.extensions

import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.EarthquakeUIState
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.models.EarthquakeVo

internal fun List<Earthquake>.mapToUi(): EarthquakeUIState {
    val earhtquakeList = this.map { item ->
        EarthquakeVo(
            id = item.id,
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
