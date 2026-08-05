package com.sgmobile.earthquake.feature.earthquake.detail.presentation.extensions

import com.sgmobile.earthquake.feature.earthquake.detail.presentation.models.EarthquakeDetailVo
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Earthquake

internal fun Earthquake.toDetailUi(): EarthquakeDetailVo =
    EarthquakeDetailVo(
        place = place,
        magnitude = magnitude,
        depth = depth,
        date = date,
        latitude = latitude.toString(),
        longitude = longitude.toString(),
    )
