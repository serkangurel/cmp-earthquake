package com.sgmobile.earthquake.feature.earthquake.detail.presentation.extensions

import com.sgmobile.earthquake.core.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetail

internal fun Earthquake.toDetailUi(): EarthquakeDetail =
    EarthquakeDetail(
        place = place,
        magnitude = magnitude,
        magnitudeThreshold = magnitudeThreshold,
        depth = depth,
        date = date,
        latitude = latitude,
        longitude = longitude,
    )
